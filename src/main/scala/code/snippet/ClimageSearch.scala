package code.snippet

import scala.xml.{NodeSeq, Text}
import scala.collection.immutable._
import net.liftweb.common._
import net.liftweb.util.Helpers._

import net.liftweb.http.SHtml
import net.liftweb.http.js.JsCmd
import net.liftweb.http.js.JsCmds.Run

import net.liftweb.json.JsonAST._
import code.model.RouteData
import code.util.Logging

class ClimageSearch extends Logging {

	object Currencies extends Enumeration {
		type Currencies = Value
		val Pound = Value("GBP")
		val Euro = Value("EUR")
		val Dollar = Value("USD")
	}

	var currentValue = Currencies.Pound
	// starting value
	implicit val formats = net.liftweb.json.DefaultFormats
	val theChart = "graphSpace"
	val chartWidth = "800"
	val chartHeight = "400"
	var currencyCombo = Currencies.Pound.toString()
	var graphCurrency = Currencies.Pound

	def view = <span>market spread viewing</span>

	def loadRoute(newCurrency: String): JsCmd = {
		Run("alert('loading...'")
	}

	def controls(xhtml: NodeSeq): NodeSeq = {
		SHtml.ajaxForm(
			bind("search", xhtml,
				"routeList" -> SHtml.ajaxSelect(RouteData.allRoutes.map(r => (r.toString(), r.toString())),
					Full(""), loadRoute _),
				"test" -> Text("test")
			))
	}
}

