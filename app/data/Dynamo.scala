package data

import com.amazonaws.services.dynamodb.AmazonDynamoDBClient
import com.amazonaws.regions.{Region, Regions}
import com.amazonaws.services.dynamodb.model._
import scala.collection.JavaConversions._
import util.Logging

/**
 * Created with IntelliJ IDEA.
 * User: szymon
 * Date: 21/04/13
 * Time: 08:18
 * To change this template use File | Settings | File Templates.
 */
object Dynamo extends Logging {

  lazy val client = {
    val c = new AmazonDynamoDBClient()
    c.setRegion(Region.getRegion(Regions.EU_WEST_1))
    c
  }

  // counterSet: meta (area/climage/route), 1 (areaId climages), 1_1 (climageId routes)
  def incrementCounter(counterSet:String, itemId:String) = {
    log.debug("CALL-dynamo:incrementcounter(%s,%s)".format(counterSet,itemId))
    val key = new Key(new AttributeValue(counterSet))
    val avu = Map(itemId -> new AttributeValueUpdate()
      .withValue(new AttributeValue().withN("1"))
      .withAction(AttributeAction.ADD))
    log.debug("updating counter: %s %s".format(counterSet, itemId))
    client.updateItem(new UpdateItemRequest("Counters", key, avu).withReturnValues(ReturnValue.ALL_NEW)).getAttributes().get(itemId).getN
  }
  def getCount(counterSet:String, itemId:String) = {
    log.debug("CALL-dynamo:getcount(%s,%s)".format(counterSet,itemId))
    getItemFieldFromTable("Counters", counterSet, itemId) match {
      case Some(counter) => counter.getN.toInt
      case None => 0
    }
  }
  def getItemFieldFromTable(tableName:String, itemId:String, fieldId:String) = {
    log.debug("CALL-dynamo:getItemFieldFromTable(%s,%s,%s)".format(tableName,itemId,fieldId))
    getItemFromTable(tableName, itemId) match {
      case Some(item) => Option(item.get(fieldId))
      case None => None
    }
  }
  def getItemFromTable(tableName:String, itemId:String) = {
    log.debug("CALL-dynamo:getItemFromTable(%s,%s)".format(tableName,itemId))
    Option(client.getItem(new GetItemRequest(tableName, new Key(new AttributeValue(itemId)))).getItem)
  }
  def getItemsFromTable(tableName:String, itemIds:List[String]) = {
    log.debug("CALL-dynamo:getItemsFromTable(%s,%s)".format(tableName,itemIds))
    client.batchGetItem(new BatchGetItemRequest().withRequestItems(Map(tableName -> new KeysAndAttributes().withKeys(
      itemIds.map{ itemId => new Key().withHashKeyElement(new AttributeValue(itemId)) }
    ))))
  }
  def putItem(tableName:String, pairs:Seq[Pair[String,String]]) {
    log.debug("CALL-dynamo:putItem(%s,%s)".format(tableName,pairs))
    val items = pairs.map{p => (p._1,new AttributeValue(p._2))}.toMap
    client.putItem(new PutItemRequest(tableName, items))
  }

}



