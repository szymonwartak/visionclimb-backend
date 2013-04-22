
import com.amazonaws.services.dynamodb.model._
import data.{Dynamo, Area}
import play.api.Play

import com.lambdaworks.jacks.JacksMapper
import org.specs2.mutable.{Before, Specification}
import play.api.http.HeaderNames
import play.api.libs.json.Json
import play.api.test._

import play.api.test.FakeApplication
import play.api.test.Helpers._

trait PreStartApp extends Before {
  def before = Play.start(FakeApplication())
}
object ClimageSpec extends Specification with PreStartApp {

  "running Climage app" should {
    "get areas" in {
      val home = route(FakeRequest(GET, "/api/route/getAreas")).get
      status(home) must equalTo(OK)
    }
		"post area" in {
			val area = Area("2", "52", "1", "birmingham")
			val json = JacksMapper.writeValueAsString(area)
			val request = FakeRequest(POST, "/api/route/postArea").withHeaders(HeaderNames.CONTENT_TYPE -> "application/json")
			val home = route(request,Json.parse(json)).get
		}
    "counter increment" in {
      val counterSet = "testCounter"
      val itemId = "1"
      val key = new Key(new AttributeValue(counterSet))
      Dynamo.client.deleteItem(new DeleteItemRequest("Counters", key))
      val newValue = Dynamo.incrementCounter(counterSet, itemId)
      assert(newValue == "1")
    }
  }

}

object TestSpec extends Specification with PreStartApp {
  "stuff" should {
    "do" in {
      println(Dynamo.getJsonItemsFromTable("Area",List("2","1")))
    }
  }
}