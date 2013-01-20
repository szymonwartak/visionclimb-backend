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
import scala.None

object ClimageUpload extends RestHelper with Logging {
	serve( "api" / "route" prefix {
		case "getAreas" :: _ JsonGet _ => {
			val areas = RouteDAO.getAreas()
			log.debug("getting all area names....."+areas)
			JArray(areas)
		}
		case "postArea" :: _ Post req => {
			val areaId = RouteDAO.insertArea(
				req.param("name") openOr "",
				req.param("latitude") openOr "0",
				req.param("longitude") openOr "0"
			)
			("areaId" -> areaId) ~ Map()
		}
		case "getAreaClimages" :: AsLong(areaId) :: _ JsonGet _ => {
			log.debug("getting area climages.....%s".format(areaId))
			JArray(RouteDAO.getAreaClimages(areaId.toString))
		}
		// route ids will come as a stringified JS array: [1,2,3]
		case "getClimage" :: climageId :: _ JsonGet _ => {
			log.debug("getting climage.....%s".format(climageId))
			RouteDAO.getClimage(climageId.toString)
		}
		case "postRouteWithImage" :: _ Post req => {
			val (routeId, climageId) = RouteDAO.insertRouteWithImage(
				req.param("areaId") openOr "",
				req.param("routeName") openOr "a route",
				req.param("grade") openOr "-",
				req.param("routePointsX") openOr "[]",
				req.param("routePointsY") openOr "[]",
				req.param("imageName") openOr "an image",
				req.param("latitude") openOr "0",
				req.param("longitude") openOr "0",
				req.param("imageData") openOr ""
			)
			("routeId" -> routeId) ~ ("climageId" -> climageId)
		}
		case "postRoute" :: _ Post req => {
			val climageId = req.param("climageId") openOr "0"
			val routeId = RouteDAO.insertRoute(
				climageId,
				req.param("routeName") openOr "a route",
				req.param("grade") openOr "-",
				req.param("routePointsX") openOr "[]",
				req.param("routePointsY") openOr "[]"
			)
			("routeId" -> routeId) ~ ("climageId" -> climageId)
		}
		case _ => () => {
			log.debug(S.param("name").toString)
			Full(JsonResponse("fail"))
		}
	})
}

