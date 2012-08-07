package code.model

import collection.mutable.ListBuffer
import com.twitter.cassie.codecs.Utf8Codec
import com.twitter.finagle.stats.NullStatsReceiver
import entity.{Area, Route}
import java.util.concurrent.atomic.AtomicInteger
import com.twitter.cassie.{CounterColumn, Column, Cluster}
import net.liftweb.json.JsonAST.{JString, JValue}
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import code.util.{JsonUtils, Logging}
import java.util
import scala.collection.JavaConversions._

object RouteData extends Logging {

}

object RouteDAO extends Logging {
	val BLANK_IMAGE = "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAIAAAACDbGyAAAAAXNSR0IArs4c6QAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oMCRUiMrIBQVkAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAADElEQVQI12NgoC4AAABQAAEiE+h1AAAAAElFTkSuQmCC"

	val climage = new Cluster(Set("localhost"), 9160, NullStatsReceiver).keyspace("CLIMAGE").connect()
	val routes = climage.columnFamily("Routes", Utf8Codec, Utf8Codec, Utf8Codec)
	val images = climage.columnFamily("Images", Utf8Codec, Utf8Codec, Utf8Codec)
	val areas = climage.columnFamily("Areas", Utf8Codec, Utf8Codec, Utf8Codec)
	val routeCounters = climage.counterColumnFamily("RouteCounters", Utf8Codec, Utf8Codec)
	val routeCounterLock = new Object()

	def getAreas() : List[JObject] = {
		var returning = List[JObject]()
		val allAreasBuffer = new ListBuffer[JObject]
		areas.rowsIteratee(100, Area.columnSet).foreach {
			case(key, columns) => {
				log.debug("columns: "+JsonUtils.getJsonFromQueryResult(columns))
				allAreasBuffer += JsonUtils.getJsonFromQueryResult(columns)
			}
		} ensure {
			log.debug(allAreasBuffer.size.toString)
			log.debug("les routes: "+allAreasBuffer.toList.toString())
			returning = allAreasBuffer.toList
		}
		while(returning.size == 0) // waiting for result - there must be a better way to do this query without this hack
			Thread.sleep(100)
		return returning
	}

	/**
	 * @return List of routes in the area.
	 *         note: No grouping happening on the back end (e.g., by imageId)
	 */
	def getAreaRoutes( areaId:String ) : List[JObject] = {
		val idSet = new util.HashSet[String]()
		(1 to routeCounters.getColumn(areaId,"count").get().get.value.toInt).foldLeft(idSet){
			(set,x) => set.add(x.toString()); set
		}
		log.debug("getting area:"+areaId+" idSet:"+idSet)
		val routeSet = new util.HashSet[String]()
		areas.getColumns(areaId, idSet).get().foldLeft(routeSet) {
			(set,x) => set.add(x._2.value); set
		}
		val json = routes.multigetColumns(routeSet, Route.areaColumnSet).get() map {
			route => { JsonUtils.getJsonFromQueryResult(route._2.values()) }
		}
		json.toList
	}

	def getRoutes( ids:List[String] ) : List[JObject] = {
		routes.multigetColumns(setAsJavaSet(ids.toSet), Route.fullColumnSet).get() map {
			route => JsonUtils.getJsonFromQueryResult(route._2.values())
		} toList
	}

	def getImage( id:String ) : JObject = {
		if(id=="0")
			("image" -> RouteDAO.BLANK_IMAGE)
		else
			JsonUtils.getJsonFromQueryResult(images.getColumns(id, Route.imageColumnSet).get().values())
	}

	def getRoute( id:String ) : JObject = {
		log.debug("getting route by id..."+id)
		val route = routes.getColumns(id, Route.fullColumnSet).get()
		log.debug("image id: "+route.get("imageId").value)
		val image = images.getColumns(route.get("imageId").value, Route.imageColumnSet).get()
		("routeId" -> route.get("routeId").value) ~
			("name" -> route.get("name").value) ~
			("routePointsX" -> route.get("routePointsX").value) ~
			("routePointsY" -> route.get("routePointsY").value) ~
			("latitude" -> route.get("latitude").value) ~
			("longitude" -> route.get("longitude").value) ~
			("image" -> image.get("image").value)
	}


	def getRouteCount( areaId:String ) : Long = {
		val count = routeCounters.getColumn(areaId,"count").get()
		count.get.value
	}

	def getRouteCountGetAndIncrement( id:String ) : String = {
		routeCounterLock.synchronized {
			val count = routeCounters.add(id, CounterColumn("count",1)).get()
			return routeCounters.getColumn(id,"count").get().get.value.toString
		}
	}

	def deleteRoute( routeId:String ) = {
		val route = routes.getColumns(routeId, Route.fullColumnSet).get()
		areas.removeColumn(route.get("areaId").value, route.get("areaRouteId").value)
		routes.removeRow(routeId)
	}

	def insertRouteWithImage( areaId:String, name:String, routePointsX:String, routePointsY:String, latitude:String, longitude:String, image:String ) : (String,String) = {
		val nextImageKey = getRouteCountGetAndIncrement("images")
		val nextRouteKey = insertRoute(areaId, name, routePointsX, routePointsY, latitude, longitude, nextImageKey)
		val imageBatch = images.batch()
		imageBatch.insert(nextImageKey, Column("imageId", nextImageKey))
		imageBatch.insert(nextImageKey, Column("image", image))
		imageBatch.execute() ensure {
			log.debug("---------------- image added")
		}
		(nextRouteKey, nextImageKey)
	}

	def insertRoute( areaId:String, name:String, routePointsX:String, routePointsY:String, latitude:String, longitude:String, imageId:String ) : String = {
		val nextRouteKey = getRouteCountGetAndIncrement("routes")
		val nextAreaRouteKey = getRouteCountGetAndIncrement(areaId)
		log.debug("inserting route... "+nextRouteKey+"_"+imageId+"_"+nextAreaRouteKey+"_"+name+"_"+routePointsX+"_"+routePointsY+"_"+latitude+"_"+longitude+"_area:"+areaId)
		val routeBatch = routes.batch()
		routeBatch.insert(nextRouteKey, Column("areaId", areaId))
		routeBatch.insert(nextRouteKey, Column("areaRouteId", nextAreaRouteKey))
		routeBatch.insert(nextRouteKey, Column("routeId", nextRouteKey))
		routeBatch.insert(nextRouteKey, Column("name", name))
		routeBatch.insert(nextRouteKey, Column("routePointsX", routePointsX))
		routeBatch.insert(nextRouteKey, Column("routePointsY", routePointsY))
		routeBatch.insert(nextRouteKey, Column("latitude", latitude))
		routeBatch.insert(nextRouteKey, Column("longitude", longitude))
		routeBatch.insert(nextRouteKey, Column("imageId", imageId))
		routeBatch.execute() ensure {
			log.debug("---------------- route added")
		}
		areas.insert(areaId, Column(nextAreaRouteKey, nextRouteKey)) ensure {
			log.debug("---------------- route added to area")
		}
		nextRouteKey
	}

}