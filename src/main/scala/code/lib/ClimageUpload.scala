package code.lib

import net.liftweb._
import common.Full
import http._
import json.Extraction
import json.JsonAST.{JInt, JString}
import rest._
import json.Serialization.write
import util.BasicTypesHelpers.AsLong
import util.Helpers.tryo
import org.slf4j.LoggerFactory

object ClimageUpload extends RestHelper {
	private[this] val logger = LoggerFactory.getLogger(getClass().getName());

	case class AppProviderInfo( name: String, description: String) {
		def toJson = Extraction.decompose(this)
	}
	case class Person(route: String)

	/*
			 * Serve the URL, but have a helpful error message when you
			 * return a 404 if the item is not found
			 */
	serve( "api" / "route" prefix {
		case "get" :: AsLong(id) :: _ JsonGet _ => JInt(id)
		case "postworks" :: _ Post req => // json is a net.liftweb.json.JsonAST.JValue
			{
				logger.debug(req.param("name").toString)
				JInt(9999)

			}
		case _ => () => {
			logger.debug(S.param("name").toString)
			Full(JsonResponse("fail"))
		}
	})

}

