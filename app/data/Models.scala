package data

import com.lambdaworks.jacks.JacksMapper
import com.amazonaws.services.dynamodb.model.{AttributeValue, ScanRequest}
import scala.collection.JavaConversions._
import scala.collection.mutable

object Run extends App {
  Area("1", "51", "0", "london").write()
  println(Area.scan())

//  println(DynamoDao.scan[Area])


}

//case class Route(routeId:String, name:String, grade:String, routePointsX:String, routePointsY:String) {
//  println("Route: %s,%s,%s,%s,%s".format(routeId, name, grade, routePointsX, routePointsY))
//  def asJson = JacksMapper.writeValueAsString(this)
//  def writeJson() = Dynamo.putItemJson(Route.tableName, routeId, asJson)
//}
//object Route {
//  val tableName = "Route"
//  def scan(limit:Int = 5) = Dynamo.scan(tableName, limit)
//}
//
case class Climage(climageId:String, latitude:String, longitude:String, name:String, imageId:String) {
  println("Climage: %s,%s,%s,%s,%s".format(climageId, latitude, longitude, name, imageId))
  def writeJson() = {
    println("CALL-climage:writeJson")
    Dynamo.putItem(Climage.tableName, Seq(("id",climageId), ("latitude",latitude), ("longitude",longitude), ("name",name), ("imageId",imageId)))
  }
}
object Climage {
  val tableName = "Climage"
  def scan(limit:Int = 5) : List[Climage] = {
    println("CALL:climage:scan")
    Dynamo.client.scan(new ScanRequest(tableName).withLimit(limit)).getItems
      .map(dynamoToClimage(_)).toList
  }
  def getClimages(climageIds:List[String]) : List[Climage] = {
    println("CALL-climage:getClimages")
    Dynamo.getItemsFromTable(tableName,climageIds).getResponses.flatMap {
      r => r._2.getItems.map(dynamoToClimage(_))
    }.toList
  }
  def getAreaClimages(areaId:String) = {
    println("CALL-climage:getAreaClimages")
    (1 to Dynamo.getCount(areaId, "count") map{n => "%s_%s".format(areaId, n) }).toList match {
      case Nil => List[Climage]()
      case keys => getClimages(keys)
    }
  }
  def getClimage(climageId:String) = {
    println("CALL-climage:getClimage")
    Dynamo.getItemFromTable(tableName, climageId) match {
      case Some(climage) => Some(dynamoToClimage(climage))
      case None => None
    }
  }
  def dynamoToClimage(a:mutable.Map[String,AttributeValue]):Climage =
    Climage(a("id").getS,a("latitude").getS,a("longitude").getS,a("name").getS,a("imageId").getS)
}

case class Area(areaId:String, latitude:String, longitude:String, name:String) {
  println("Area: %s,%s,%s,%s".format(areaId, latitude, longitude, name))
  def write() = {
    println("CALL-area:write")
    Dynamo.putItem(Area.tableName, Seq(("id",areaId), ("latitude",latitude), ("longitude",longitude), ("name",name)))
  }
}
object Area {
  val tableName = "Area"
  def scan(limit:Int = 5) : List[Area] = {
    println("CALL-area:scan")
    Dynamo.client.scan(new ScanRequest(tableName).withLimit(limit)).getItems
      .map(dynamoToArea(_)).toList
  }
  def getAreas(areaIds:List[String]) : List[Area] = {
    println("CALL-area:getAreas")
    Dynamo.getItemsFromTable(tableName,areaIds).getResponses.flatMap {
      r => r._2.getItems.map(dynamoToArea(_))
    }.toList
  }
  def dynamoToArea(a:mutable.Map[String,AttributeValue]):Area =
    Area(a("id").getS,a("latitude").getS,a("longitude").getS,a("name").getS)
}


//object DynamoCaseClassMap extends DynamoCaseClassMap("")
//abstract class DynamoCaseClassMap(id:String) {
//  val tableName = {val tn = this.getClass.getSimpleName; println("tn"+tn); tn.substring(0,tn.size-1)}
//  def scan(limit:Int = 5) = Dynamo.client.scan(new ScanRequest(tableName).withLimit(limit)).getItems.map(_.get("jsonObj").getS)
//  def asMap =
//    (Map[String, AttributeValue]() /: this.getClass.getDeclaredFields) {(a, f) =>
//      f.setAccessible(true)
//      a + (f.getName -> new AttributeValue(f.get(this).toString))
//    }
//  def asJson = "{}"
//  def writeJson() = Dynamo.client.putItem(new PutItemRequest(tableName,Map("id" -> new AttributeValue(id), "jsonObj" -> new AttributeValue(asJson))))
//}


//object DynamoDao {
//  def write[T](item:T) = {
//    val itemJson = JacksMapper.writeValueAsString(item)
//    println(itemJson)
//    println(item.getClass.getSimpleName)
////    Dynamo.client.putItem(new PutItemRequest(tableName,Map("jsonObj" -> new AttributeValue(itemJson))))
//  }
//  def scan[T](implicit cm:Manifest[T]) = {
//    cm
//  }//Dynamo.client.scan(new ScanRequest().withLimit(limit)).getItems.map(_.get("jsonObj").getS)
//}


// arn:aws:dynamodb:eu-west-1:visionclimb:table/area
// arn:aws:simpledb:eu-west-1:visionclimb:table/area