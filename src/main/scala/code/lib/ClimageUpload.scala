package code.lib

import net.liftweb._
import common.Full
import http._
import json.JsonAST._
import json.JsonDSL._
import rest._
import util.BasicTypesHelpers.AsLong
import code.model.{RouteDAO, RouteData}
import code.util.Logging
import code.model.entity.Route

object ClimageUpload extends RestHelper with Logging {
	serve( "api" / "route" prefix {
		case "get" :: AsLong(routeId) :: _ JsonGet _ => {
			log.debug("getting route.....")
			RouteDAO.getRoute(routeId.toString)
		}
		case "getAreas" :: _ JsonGet _ => {
			val areas = RouteDAO.getAreas()
			log.debug("getting all area names....."+areas)
			JArray(areas)
		}
		case "getAreaRoutes" :: AsLong(areaId) :: _ JsonGet _ => {
			log.debug("getting area routes.....")
			JArray(RouteDAO.getAreaRoutes(areaId.toString))
		}
		// route ids will come as a stringified JS array: [1,2,3]
		case "getRoutesAndImage" :: _ Post req => {
			val routeIds = (req.param("routeIds") openOr "")
			log.debug("getting multiple routes...."+routeIds)
			val routes = RouteDAO.getRoutes("""[\d]+""".r findAllIn routeIds map {_.toString} toList)
			val image = RouteDAO.getImage(req.param("imageId") openOr "0")
			image ~ ("routes" -> JArray(routes))
		}
		case "postRouteWithImage" :: _ Post req => {
			val (routeId, imageId) = RouteDAO.insertRouteWithImage(
				req.param("areaId") openOr "",
				req.param("name") openOr "a route",
				req.param("routePointsX") openOr "[]",
				req.param("routePointsY") openOr "[]",
				req.param("latitude") openOr "0",
				req.param("longitude") openOr "0",
				req.param("image") openOr ""
			)
			("routeId" -> routeId) ~ ("imageId" -> imageId)
		}
		case "postRoute" :: _ Post req => {
			val imageId = req.param("imageId") openOr "0"
			val routeId = RouteDAO.insertRoute(
				req.param("areaId") openOr "",
				req.param("name") openOr "a route",
				req.param("routePointsX") openOr "[]",
				req.param("routePointsY") openOr "[]",
				req.param("latitude") openOr "0",
				req.param("longitude") openOr "0",
				imageId
			)
			("routeId" -> routeId) ~ ("imageId" -> imageId)
		}
		case _ => () => {
			log.debug(S.param("name").toString)
			Full(JsonResponse("fail"))
		}
	})
}

