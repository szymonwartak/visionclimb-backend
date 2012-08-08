import code.model.RouteDAO
import code.util.JsonUtils
import com.twitter.cassie.Column
import java.util
import net.liftweb.json.JsonAST.JArray
import org.scalatest.fixture.FixtureFunSuite
import org.scalatest.Tag

class MeTest extends FixtureFunSuite {
	type FixtureParam = Map[String, Any]

	override def withFixture(test: OneArgTest) {
		test(test.configMap)
	}

	test("1", Tag("dood1")) {
		conf => {
//			val list = new util.ArrayList[Column[String,String]]()
//			list.add(new Column("1","london"))
//			list.add(new Column("2","birmingham"))
//			println(JsonUtils.getJsonFromQueryResult(list))
//			println("------------"+RouteDAO.getAreas())

			RouteDAO.insertRouteWithImage("1","route1","5.10b","[50,100]","[50,100]","53","0.01","data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAIAAAACDbGyAAAAAXNSR0IArs4c6QAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oMCRUiMrIBQVkAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAADElEQVQI12NgoC4AAABQAAEiE+h1AAAAAElFTkSuQmCC")
			RouteDAO.insertRoute("1","route2","5.10b","[20,70]","[20,70]","53","0","1")
			RouteDAO.insertRoute("1","route3","5.10c","[70,20]","[20,70]","53","0","1")
			RouteDAO.insertRouteWithImage("2","route1","5.10b","[50,100]","[50,`00]","54","-1","data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAAAUAAAAFCAIAAAACDbGyAAAAAXNSR0IArs4c6QAAAAlwSFlzAAALEwAACxMBAJqcGAAAAAd0SU1FB9oMCRUiMrIBQVkAAAAZdEVYdENvbW1lbnQAQ3JlYXRlZCB3aXRoIEdJTVBXgQ4XAAAADElEQVQI12NgoC4AAABQAAEiE+h1AAAAAElFTkSuQmCC")
			RouteDAO.insertRoute("2","route2","5.10b","[20,10]","[100,10]","54","-1.01","2")

//			println(RouteDAO.getAreaRoutes("2"))
//			println("""\d""".r findAllIn "[1,2,3]" toList)
		}
	}
}
