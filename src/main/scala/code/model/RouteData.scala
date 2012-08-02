package code.model

import collection.mutable.ListBuffer
import com.twitter.cassie.codecs.Utf8Codec
import com.twitter.finagle.stats.NullStatsReceiver
import java.util.concurrent.atomic.AtomicInteger
import com.twitter.cassie.{Column, Cluster}
import net.liftweb.json.JsonAST.{JString, JValue}
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import code.util.Logging

object RouteData extends Logging {
	// initialize the climage sequence counter (this will have to be replaced by a better unique id?
	var foundRouteSeqId = true; val routeSeqId = new AtomicInteger(0);
	while(foundRouteSeqId) {
		foundRouteSeqId = false
		val nextRoute = RouteDAO.routes.getColumns(routeSeqId.incrementAndGet().toString, Route.columnSet).get()
		if( nextRoute.size()>0 ) {
			log.debug("got - "+nextRoute.keySet().toString)
			foundRouteSeqId = true
		}
	}
	log.debug("initial route seq id is: "+routeSeqId)
	// initialize the image sequence counter (this will have to be replaced by a better unique id?
	var foundImageSeqId = true; val imageSeqId = new AtomicInteger(0);
	while(foundImageSeqId) {
		foundImageSeqId = false
		val nextImage = RouteDAO.images.getColumns(imageSeqId.incrementAndGet().toString, Route.columnSet).get()
		if( nextImage.size()>0 ) {
			log.debug("got - "+nextImage.keySet().toString)
			foundImageSeqId = true
		}
	}
	log.debug("initial image seq id is: "+imageSeqId)

	def refreshRoutes() = {
		val allRoutesBuffer = new ListBuffer[JObject]
		RouteDAO.routes.rowsIteratee(100, Route.columnSet).foreach {
			case(key, columns) => {
				if("id".equals(columns.get(0).name)&&"name".equals(columns.get(1).name))
					allRoutesBuffer += ("id" -> columns.get(0).value) ~ ("name" -> columns.get(1).value)
			}
		} ensure {
			log.debug(allRoutesBuffer.size.toString)
			allRoutes = allRoutesBuffer.toList
			log.debug("les routes: "+allRoutes.toString())
		}
	}
	var allRoutes = List[JObject]()
	refreshRoutes()
}

object RouteDAO extends Logging {
	val climage = new Cluster(Set("localhost"), 9160, NullStatsReceiver).keyspace("CLIMAGE").connect()
	val routes = climage.columnFamily("Routes", Utf8Codec, Utf8Codec, Utf8Codec)
	val images = climage.columnFamily("Images", Utf8Codec, Utf8Codec, Utf8Codec)

	def insertRoute( name:String, routePointsX:String, routePointsY:String, latitude:String, longitude:String, image:String ) = {
		val nextRouteKey = RouteData.routeSeqId.getAndIncrement.toString
		val nextImageKey = RouteData.imageSeqId.getAndIncrement.toString
		log.debug("inserting route... "+nextRouteKey+"_"+name+"_"+routePointsX+"_"+routePointsY+"_"+latitude+"_"+longitude+"_"+nextImageKey)
		val routeBatch = routes.batch()
		routeBatch.insert(nextRouteKey, Column("id", nextRouteKey))
		routeBatch.insert(nextRouteKey, Column("name", name))
		routeBatch.insert(nextRouteKey, Column("routePointsX", routePointsX))
		routeBatch.insert(nextRouteKey, Column("routePointsY", routePointsY))
		routeBatch.insert(nextRouteKey, Column("latitude", latitude))
		routeBatch.insert(nextRouteKey, Column("longitude", longitude))
		routeBatch.insert(nextRouteKey, Column("imageId", nextImageKey))
		routeBatch.execute() ensure {
			log.debug("---------------- route added")
			RouteData.refreshRoutes()
		}
		val imageBatch = images.batch()
		imageBatch.insert(nextImageKey, Column("id", nextImageKey))
		imageBatch.insert(nextImageKey, Column("image", image))
		imageBatch.execute() ensure {
			log.debug("---------------- image added")
		}
	}

	def getByRouteId( id:Long ) : JObject = {
		log.debug("getting route by id..."+id)
		val route = routes.getColumns(id.toString, Route.fullColumnSet).get()
		log.debug("image id: "+route.get("imageId").value)
		val image = images.getColumns(route.get("imageId").value, Route.imageColumnSet).get()
		("id" -> route.get("id").value) ~
			("name" -> route.get("name").value) ~
			("routePointsX" -> route.get("routePointsX").value) ~
			("routePointsY" -> route.get("routePointsY").value) ~
			("latitude" -> route.get("latitude").value) ~
			("longitude" -> route.get("longitude").value) ~
			("image" -> image.get("image").value)
	}

}