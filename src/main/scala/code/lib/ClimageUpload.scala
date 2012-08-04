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
			log.debug("getting route.....")
			RouteDAO.getByRouteId(id.toString)
		}
		case "getAreas" :: _ JsonGet _ => {
			val areas = RouteDAO.getAreas()
			log.debug("getting all area names....."+areas)
			JArray(areas)
		}
		case "getAreaRoutes" :: AsLong(id) :: _ JsonGet _ => {
			log.debug("getting area routes.....")
			JArray(RouteDAO.getAreaRoutes(id.toString))
		}
		case "postworks" :: _ Post req => {
			RouteDAO.insertRoute(
				req.param("areaId") openOr "",
				req.param("name") openOr "a route",
				req.param("routePointsX") openOr "[]",
				req.param("routePointsY") openOr "[]",
				req.param("latitude") openOr "0",
				req.param("longitude") openOr "0",
				req.param("image") openOr ""
			)
			JInt(9999)
		}
		case _ => () => {
			log.debug(S.param("name").toString)
			Full(JsonResponse("fail"))
		}
	})
}

