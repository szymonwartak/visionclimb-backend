import code.model.RouteDAO
import code.util.JsonUtils
import com.twitter.cassie.Column
import java.util
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

//			RouteDAO.insertRoute("1","route1","100,200","100,200","53","1.01","png")
//			RouteDAO.insertRoute("1","route2","200,100","100,200","53","1","png")
//			RouteDAO.insertRoute("2","route1","100,200","100,200","54","0","png")
//			RouteDAO.insertRoute("2","route2","200,100","100,200","54","0.01","png")

			println(RouteDAO.getAreaRoutes("1"))
		}
	}
}