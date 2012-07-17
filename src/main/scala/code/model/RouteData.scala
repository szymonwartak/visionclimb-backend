package code.model

import collection.mutable.ListBuffer
import com.twitter.cassie.codecs.Utf8Codec
import com.twitter.finagle.stats.NullStatsReceiver
import java.util.concurrent.atomic.AtomicInteger
import com.twitter.cassie.{Column, Cluster}
import net.liftweb.json.JsonAST.{JString, JValue}
import code.util.Logging

object RouteData extends Logging {
	// initialize the image sequence counter (this will have to be replaced by a better unique id?
	var foundSeqId = true; val seqIdCount = new AtomicInteger(0);
	while(foundSeqId) {
		foundSeqId = false
		val nextRoute = RouteDAO.routes.getColumns(seqIdCount.incrementAndGet().toString, Route.columnSet).get()
		if( nextRoute.size()>0 ) {
			log.debug("got - "+nextRoute.size()+"... "+nextRoute.get("name").value)
			foundSeqId = true
		}
	}
	log.debug("initial seq id is: "+seqIdCount)

	private val allRoutesBuffer = new ListBuffer[JString]
	var allRoutes = List[JString]()
	RouteDAO.routes.rowsIteratee(100, "name").foreach {
		case(key, columns) => allRoutesBuffer += JString(columns.get(0).value)
	} ensure {
		allRoutes = allRoutesBuffer.toList
	}
}

object RouteDAO extends Logging {
	val climage = new Cluster(Set("localhost"), 9160, NullStatsReceiver).keyspace("CLIMAGE").connect()
	val routes = climage.columnFamily("Routes", Utf8Codec, Utf8Codec, Utf8Codec)

	def insertRoute( name:String, xCoords:String, yCoords:String ) = {
		log.debug("inserting route...")
		val nextKey = RouteData.seqIdCount.getAndIncrement.toString
		val batch = routes.batch()
		batch.insert(nextKey, Column("name", name))
		batch.insert(nextKey, Column("xCoords", xCoords))
		batch.insert(nextKey, Column("yCoords", yCoords))
		batch.execute() ensure {
			log.debug("---------------- added")
		}
	}

	def getByRouteId( id:Long ) : JString = {
		log.debug("getting route by id...")
		var retval = JString("none?")
		routes.getColumns(id.toString, Route.columnSet) map { _ match {
			case route: Map[String, Column[String, String]] => {
				log.debug("got - "+route.size+"... "+route.get("name").get.value)
				retval = JString(route.get("name").get.value)
			}
			case _ => log.debug("NO MATCH??")
		}}
		retval
	}

}