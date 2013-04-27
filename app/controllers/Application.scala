package controllers

import data._

import play.api.mvc._
import com.lambdaworks.jacks.JacksMapper
import play.api.libs.json._
import scala.Some

object Application extends Controller {

  def getAreas = Action {
    val result = Area.scan()
    println("getAreas:"+result)
    Ok(result)
  }

  def postArea = Action(parse.urlFormEncoded) { request =>
    val nextAreaId = Dynamo.incrementCounter("area", "count")
    // TODO: reject areas with no lat/lng
    val area = Area(nextAreaId,
      request.body.getOrElse("latitude", Set("0")).head,
      request.body.getOrElse("longtidue", Set("0")).head,
      request.body.getOrElse("name", Set("-")).head
    )
    area.writeJson()
    Ok("""{"nextAreaId":%s}""".format(nextAreaId))
  }
	def postAreaJson = Action(parse.json) { request =>
		request.body.validate(Json.reads[Area]).map { area =>
			println("postArea: "+area)
      area.writeJson()
			Ok("ok")
		}.get
	}
  def getAreaClimages(areaId:String) = Action {
    val json = Climage.getAreaClimages(areaId)
    println("getAreaClimages:"+json)
    Ok(json)
  }
  def getClimage(climageId:String) = Action {
    val json = Climage.getClimage(climageId)
    println("getClimage:"+json)
    Ok(json)
  }
  def postRouteWithImage = Action(parse.multipartFormData) { request =>
    // TODO: climage with no lat/lng defaults to area info
    val s3ImageId = request.body.file("picture") match {
      case Some(file) => S3.putFile(file.ref.file)
      case None => "error"
    }

    val areaId = request.body.dataParts.getOrElse("areaId", Set("")).head
    val nextClimageId = "%s_%s".format(areaId, Dynamo.incrementCounter(areaId, "count"))
    val nextRouteId = "%s_%s".format(nextClimageId, Dynamo.incrementCounter(nextClimageId, "count"))
    println("area:%s climage:%s route:%s".format(areaId, nextClimageId, nextRouteId))
    val climage = Climage(nextClimageId,
      request.body.dataParts.getOrElse("latitude",Set("")).head,
      request.body.dataParts.getOrElse("longitude",Set("")).head,
      request.body.dataParts.getOrElse("imageName",Set("")).head,
      s3ImageId
    )
    println(climage)
    climage.writeJson()
    val route = Route(nextRouteId,
      request.body.dataParts.getOrElse("routeName",Set("")).head,
      request.body.dataParts.getOrElse("grade",Set("")).head,
      request.body.dataParts.getOrElse("routePointsX",Set("")).head,
      request.body.dataParts.getOrElse("routePointsY",Set("")).head
    )
    println(route)
    route.writeJson()

    Ok("""{"route":%s,"imageId":%s}""".format(nextRouteId, nextClimageId))
  }
  def postRoute = Action(parse.urlFormEncoded) { request =>
    val climageId = request.body.getOrElse("climageId", Set("0")).head
    val nextRouteId = Dynamo.incrementCounter(climageId, "count")
    val route = Route(nextRouteId,
      request.body.getOrElse("routeName", Set("0")).head,
      request.body.getOrElse("grade", Set("0")).head,
      request.body.getOrElse("routePointsX", Set("0")).head,
      request.body.getOrElse("routePointsY", Set("0")).head
    )
    route.writeJson()
    Ok("""{"route":%s,"imageId":%s}""".format(nextRouteId, climageId))
  }

}

//case "postRoute" :: _ Post req => {
//val climageId = req.param("climageId") openOr "0"
//val routeId = RouteDAO.insertRoute(
//climageId,
//req.param("routeName") openOr "a route",
//req.param("grade") openOr "-",
//req.param("routePointsX") openOr "[]",
//req.param("routePointsY") openOr "[]"
//)
//log.debug("SERVER\t%s\tpostRoute\t%s\t%s".format(req.param("userId") openOr "ANON", req.params, ("routeId" -> routeId) ~ ("climageId" -> climageId)))
//("routeId" -> routeId) ~ ("climageId" -> climageId)