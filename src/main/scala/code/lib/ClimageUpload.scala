package code.lib

import net.liftweb._
import common.Full
import http._
import json.JsonAST._
import rest._
import util.BasicTypesHelpers.AsLong
import code.model.{RouteDAO, RouteData}
import code.util.Logging

object ClimageUpload extends RestHelper with Logging {
	serve( "api" / "route" prefix {
		case "get" :: AsLong(id) :: _ JsonGet _ => {
			log.debug("getting data.....")
			RouteDAO.getByRouteId(id)
		}
		case "getRoutes" :: _ JsonGet _ => {
			log.debug("getting all route names.....")
			JArray(RouteData.allRoutes)
		}
		case "postworks" :: _ Post req => {
			RouteDAO.insertRoute(
				req.param("name") openOr "a route",
				req.param("xCoords") openOr "[]",
				req.param("yCoords") openOr "[]"
			)
			JInt(9999)
		}
		case _ => () => {
			log.debug(S.param("name").toString)
			Full(JsonResponse("fail"))
		}
	})

}

