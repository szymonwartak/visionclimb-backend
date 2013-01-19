package code.model

import collection.mutable.ListBuffer
import com.twitter.cassie.codecs.Utf8Codec
import com.twitter.finagle.stats.NullStatsReceiver
import entity.{Climage, Area, Route}
import java.util.concurrent.atomic.AtomicInteger
import com.twitter.cassie.{CounterColumn, Column, Cluster}
import net.liftweb.json.JsonAST.{JString, JValue}
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import code.util.{JsonUtils, Logging}
import java.util.{Set => JSet}
import scala.collection.JavaConversions._
import JsonUtils.{getJsonFromQueryResult => ToJson}

object RouteData extends Logging {

}

object RouteDAO extends Logging {
	val BLANK_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAIAAAACDbGyAAAAAXNSR0IArs4c6QAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oMCRUiMrIBQVkAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAADElEQVQI12NgoC4AAABQAAEiE+h1AAAAAElFTkSuQmCC"

	val cass = new Cluster(Set("localhost"), 9160, NullStatsReceiver).keyspace("CLIMAGE").connect()
	val routes = cass.columnFamily("Routes", Utf8Codec, Utf8Codec, Utf8Codec)
	val climages = cass.columnFamily("Climages", Utf8Codec, Utf8Codec, Utf8Codec)
	val areas = cass.columnFamily("Areas", Utf8Codec, Utf8Codec, Utf8Codec)
	val counters = cass.counterColumnFamily("Counters", Utf8Codec, Utf8Codec) // Counters[type:climage/route][id:areaId/climageId]
	val counterLocks = Map(
			"meta" -> new Object(), // counting total area/climage/route
			"area" -> new Object(), // counting areas
			"climage" -> new Object(), // counting climages for an areaId
			"route" -> new Object()) // counting routes for a climageId

	def getCount( name:String, areaId:String ) : Long = {
		val count = counters.getColumn(name, areaId).get()
		count.get.value
	}
	def incrementCountAndGet( name:String, id:String ) : String = {
		counterLocks(name).synchronized {
			counters.add("meta", CounterColumn(name,1)).get() // increment type counter
			counters.add(name, CounterColumn(id,1)).get()
			return counters.getColumn(name, id).get().get.value.toString
		}
	}

	def insertArea( name:String, latitude:String, longitude:String ) = {
		val nextAreaKey = incrementCountAndGet("area", "all")
		val areaBatch = areas.batch()
		areaBatch.insert(nextAreaKey, Column("areaId", nextAreaKey))
		areaBatch.insert(nextAreaKey, Column("name", name))
		areaBatch.insert(nextAreaKey, Column("latitude", latitude))
		areaBatch.insert(nextAreaKey, Column("longitude", longitude))
		areaBatch.execute()
			.onSuccess(value => log.debug("---------------- area added! %s".format(value)))
			.onFailure(value => log.error("---------------- area add FAILED! %s".format(value)))
		nextAreaKey
	}

	def insertRouteWithImage( areaId:String, routeName:String, grade:String, routePointsX:String, routePointsY:String, 
														imageName:String, latitude:String, longitude:String, image:String ) : (String,String) = {
		val nextImageKey = "%s_%s".format(areaId, incrementCountAndGet("climage", areaId))
		val nextRouteKey = insertRoute( nextImageKey, routeName, grade, routePointsX, routePointsY)
		val imageBatch = climages.batch()
		imageBatch.insert(nextImageKey, Column("climageId", nextImageKey))
		imageBatch.insert(nextImageKey, Column("name", imageName))
		imageBatch.insert(nextImageKey, Column("latitude", latitude))
		imageBatch.insert(nextImageKey, Column("longitude", longitude))
		imageBatch.insert(nextImageKey, Column("imageData",
			(if (image.length()>=10 && image.substring(0,10)=="data:image") BLANK_IMAGE else image)))
		imageBatch.execute()
			.onSuccess(value => log.debug("---------------- image added! %s".format(nextImageKey)))
			.onFailure(value => log.error("---------------- image add FAILED! %s".format(nextImageKey)))
		(nextRouteKey, nextImageKey)
	}

	def insertRoute( climageId:String, name:String, grade:String, routePointsX:String, routePointsY:String ) : String = {
		val nextRouteKey = "%s_%s".format(climageId, incrementCountAndGet("route", climageId))
		log.debug("inserting route... "+nextRouteKey+"_"+name+"_"+grade+"_"+routePointsX+"_"+routePointsY)
		val routeBatch = routes.batch()
		routeBatch.insert(nextRouteKey, Column("routeId", nextRouteKey))
		routeBatch.insert(nextRouteKey, Column("name", name))
		routeBatch.insert(nextRouteKey, Column("grade", grade))
		routeBatch.insert(nextRouteKey, Column("routePointsX", routePointsX))
		routeBatch.insert(nextRouteKey, Column("routePointsY", routePointsY))
		routeBatch.execute() ensure {
			log.debug("---------------- route added: %s".format(nextRouteKey))
		}
		nextRouteKey
	}

	def getAreas() : List[JObject] = {
		val allAreasBuffer = new ListBuffer[JObject]
		areas.rowsIteratee(100, Area.columnSet).foreach {
			case(key, columns) => {
				log.debug("columns: "+ToJson(columns))
				allAreasBuffer += ToJson(columns)
			}
		}.get()

		log.debug("les areas: "+allAreasBuffer.toList.toString())
		return allAreasBuffer.toList
	}

	def getAreaClimages( areaId:String ) : List[JObject] = {
		val idSet = (1 to counters.getColumn("climage", areaId).get().get.value.toInt)
			.foldLeft(Set[String]()) ((set,x) =>
				set + "%s_%s".format(areaId, x)
			)
		log.debug("getting area:"+areaId+" idSet:"+idSet)
		climages.multigetColumns(idSet, Climage.columnSet).get() map {
			climage => { ToJson(climage._2.values()) }
		} toList
	}

	def getClimage( climageId:String ) : JObject = {
		log.debug("getting climage: %s".format(climageId))
		val routeIdSet = (1 to counters.getColumn("route", climageId).get().get.value.toInt)
			.foldLeft(Set[String]()) ((set,x) =>
				set + "%s_%s".format(climageId, x)
			)
		val jsonRoutes = getRoutes(routeIdSet)
		val jsonClimage = ToJson(climages.getColumns(climageId, Climage.fullColumnSet).get().values())
		jsonClimage ~ ("routes" -> jsonRoutes)
	}

	def getRoutes( idSet:Set[String] ) : List[JObject] = {
		log.debug("getting routes:"+idSet)
		routes.multigetColumns(idSet, Route.fullColumnSet).get() map {
			route => { ToJson(route._2.values()) }
		} toList
	}

//	def getImage( id:String ) : JObject = {
//		if(id=="0")
//			("image" -> RouteDAO.BLANK_IMAGE)
//		else
//			ToJson(climages.getColumns(id, Climage.imageColumnSet).get().values())
//	}
//
//	def deleteRoute( routeId:String ) = {
//		val route = routes.getColumns(routeId, Route.fullColumnSet).get()
//		areas.removeColumn(route.get("areaId").value, route.get("areaRouteId").value)
//		routes.removeRow(routeId)
//	}

}

object qqq extends App {
	val areaId1 = RouteDAO.insertArea("london", "51", "0")
	val (routeId1, imageId1) = RouteDAO.insertRouteWithImage(areaId1, "route", "5.4", "[1,2,3]", "[1,2,3]", "image", "51", "0", "image:data")
	val (routeId2, imageId2) = RouteDAO.insertRouteWithImage(areaId1, "route", "5.4", "[1,2,3]", "[1,2,3]", "image", "51", "0", "image:data")
	val areaId2 = RouteDAO.insertArea("north", "52", "0")
	val (routeId3, imageId3) = RouteDAO.insertRouteWithImage(areaId2, "route", "5.4", "[1,2,3]", "[1,2,3]", "image", "52", "0", "image:data")
	println("finished: %s %s %s %s %s".format(areaId1, imageId1, routeId1, imageId2, routeId2))
	println("finished: %s %s %s".format(areaId2, imageId3, routeId3))

	println(RouteDAO.getAreas())
	println(RouteDAO.getAreaClimages("1"))
	println(RouteDAO.getClimage("1_1"))

	System.exit(0)
}