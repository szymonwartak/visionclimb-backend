package data

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.dynamodb.model._
import scala.collection.JavaConversions._
import com.lambdaworks.jacks.JacksMapper

/**
 * Created with IntelliJ IDEA.
 * User: szymon
 * Date: 21/04/13
 * Time: 08:18
 * To change this template use File | Settings | File Templates.
 */
object Dynamo {

  lazy val client = {
    val c = new AmazonDynamoDBClient()
    c.setRegion(Region.getRegion(Regions.EU_WEST_1))
    c
  }

  def incrementCounter(counterSet:String, itemId:String) = {
    val key = new Key(new AttributeValue(counterSet))
    val avu = Map(itemId -> new AttributeValueUpdate()
      .withValue(new AttributeValue().withN("1"))
      .withAction(AttributeAction.ADD))
    client.updateItem(new UpdateItemRequest("Counters", key, avu).withReturnValues(ReturnValue.ALL_NEW)).getAttributes().get(itemId).getN
  }
  def getCount(counterSet:String, itemId:String) = getItemFieldFromTable("Counters", counterSet, itemId).getN.toInt

  def scan(tableName:String, limit:Int) =
    JacksMapper.writeValueAsString(client.scan(new ScanRequest(tableName).withLimit(limit)).getItems.map(_.get("jsonObj").getS))
  def getJsonItemsFromTable(tableName:String, keys:Seq[String]) = {
    val items = client
      .batchGetItem(new BatchGetItemRequest().withRequestItems(Map(tableName -> new KeysAndAttributes()
        .withKeys(keys.map{ key => new Key(new AttributeValue(key)) } ))))
      .getResponses().get(tableName).getItems
      .flatMap{ climage => Option(climage.get("jsonObj")) }
      .map{ climage => climage.getS }
    JacksMapper.writeValueAsString(items)
  }
  def getJsonItemFromTable(tableName:String, id:String) =
    Option(getItemFromTable(tableName, id).get("jsonObj")) match {
      case Some(jsonObj) => jsonObj.getS
      case None => "{}"
    }
  def getItemFieldFromTable(tableName:String, itemId:String, fieldId:String) =
    getItemFromTable(tableName, itemId).get(fieldId)
  def getItemFromTable(tableName:String, itemId:String) =
    client.getItem(new GetItemRequest(tableName, new Key(new AttributeValue(itemId)))).getItem

  def putItem(tableName:String, pairs:Seq[Pair[String,String]]) {
    client.putItem(new PutItemRequest(tableName, pairs.map{p => (p._1,new AttributeValue(p._2))}.toMap))
  }
  def putItemJson(tableName:String, id:String, jsonObj:String) {
    putItem(tableName, List(("id",id), ("jsonObj",jsonObj)))
  }

}

case class JsonObjWrapper(jsonObj:String, id:String)


