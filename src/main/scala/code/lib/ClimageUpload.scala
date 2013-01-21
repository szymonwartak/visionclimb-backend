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
		case "log" :: userId :: _ Post req => {
			log.debug("CLIENT\t%s\t%s\t%s".format(userId, req.param("url") openOr "", req.param("message") openOr ""))
			JString("ok")
		}
		case "getAreas" :: userId :: _ JsonGet _ => {
			val areas = RouteDAO.getAreas()
			log.debug("SERVER\tgetAreas\t%s".format(areas))
			JArray(areas)
		}
		case "postArea" :: _ Post req => {
			val areaId = RouteDAO.insertArea(
				req.param("name") openOr "",
				req.param("latitude") openOr "0",
				req.param("longitude") openOr "0"
			)
			log.debug("SERVER\t%s\tpostArea\t%s\t%s".format(req.param("userId") openOr "ANON", req.params, areaId))
			("areaId" -> areaId) ~ List()
		}
		case "getAreaClimages" :: AsLong(areaId) :: userId :: _ JsonGet _ => {
			log.debug("SERVER\tgetAreaClimages\t%s".format(areaId))
			JArray(RouteDAO.getAreaClimages(areaId.toString))
		}
		// route ids will come as a stringified JS array: [1,2,3]
		case "getClimage" :: climageId :: userId :: _ JsonGet _ => {
			log.debug("SERVER\tgetClimage\t%s".format(climageId))
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
			log.debug("SERVER\t%s\tpostRouteWithImage\t%s\t%s".format(req.param("userId") openOr "ANON", req.params.filter(_._1!="imageData"), ("routeId" -> routeId) ~ ("climageId" -> climageId)))
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
			log.debug("SERVER\t%s\tpostRoute\t%s\t%s".format(req.param("userId") openOr "ANON", req.params, ("routeId" -> routeId) ~ ("climageId" -> climageId)))
			("routeId" -> routeId) ~ ("climageId" -> climageId)
		}
		case _ => () => {
			log.debug("SERVER\tFAIL")
			Full(JsonResponse("fail"))
		}
	})
}

