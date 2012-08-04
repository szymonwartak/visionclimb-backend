package code.util

import com.twitter.cassie.Column
import net.liftweb.json.JsonAST.{JObject, JString, JValue}
import net.liftweb.json._
import net.liftweb.json.JsonDSL._
import java.util.{Collection,List,ArrayList}
import java.util
import scala.collection.JavaConversions._

object JsonUtils {

	def getJsonFromQueryResult( result:Collection[Column[String,String]]) : JObject = {
		val json = JObject(Nil)
		result.foldLeft(json){
			(list,x) => list ~ (x.name -> x.value)
		}
	}
}
