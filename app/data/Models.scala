package data

import com.amazonaws.services.dynamodb.model._
import scala.collection.JavaConversions._
import play.api.Logger
import com.lambdaworks.jacks.JacksMapper


object Run extends App {
  Area("1", "51", "0", "london").writeJson()
  println(Area.scan())

//  println(DynamoDao.scan[Area])


}

case class Route(id:String, name:String, grade:String, routePointsX:String, routePointsY:String) {
  def asJson = JacksMapper.writeValueAsString(this)
  def writeJson() = Dynamo.putItemJson(Route.tableName, id, asJson)
}
object Route {
  val tableName = "Route"
  def scan(limit:Int = 5) = Dynamo.scan(tableName, limit)
}

case class Climage(id:String, latitude:String, longitude:String, name:String, imageUrl:String) {
  def asJson = JacksMapper.writeValueAsString(this)
  def writeJson() = Dynamo.putItemJson(Climage.tableName, id, asJson)
}
object Climage {
  val tableName = "Climage"
  def scan(limit:Int = 5) = Dynamo.scan(tableName, limit)
  def getAreaClimages(areaId:String) = {
    val keys = 1 to Dynamo.getCount(areaId, "count") map{n => "%s_%s".format(areaId, n) }
    Dynamo.getJsonItemsFromTable(tableName, keys)
  }
  def getClimage(climageId:String) = Dynamo.getJsonItemFromTable(tableName, climageId)
}

case class Area(id:String, latitude:String, longitude:String, name:String) {
  def asJson = JacksMapper.writeValueAsString(this)
  def writeJson() = Dynamo.putItemJson(Area.tableName, id, asJson)
}
object Area {
  val tableName = "Area"
  def scan(limit:Int = 5) = Dynamo.scan(tableName, limit)
  def getAreas(areaIds:List[String]) = Dynamo.getJsonItemsFromTable(tableName, areaIds)
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