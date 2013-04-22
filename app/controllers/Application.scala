package controllers

import data.{Climage, Area}

import play.api.mvc._
import com.lambdaworks.jacks.JacksMapper
import play.api.libs.json._

object Application extends Controller {

  def getAreas = Action {
    val result = Area.scan()
    println("getAreas:"+result)
    Ok(result)
  }

	def postArea = Action(parse.json) { request =>
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
  def postRouteWithImage = Action(parse.json) { request =>
    request.body.validate(Json.reads[Area]).map { area =>
      println("postArea: "+area)
      val (routeId,imageId) = area.writeJson()
      Ok("""{"route":%s,"imageId":%s""".format(routeId,imageId))
    }.get
  }

//  req.param("areaId") openOr "",
//  req.param("routeName") openOr "a route",
//  req.param("grade") openOr "-",
//  req.param("routePointsX") openOr "[]",
//  req.param("routePointsY") openOr "[]",
//  req.param("imageName") openOr "an image",
//  req.param("latitude") openOr "0",
//  req.param("longitude") openOr "0",
//  req.param("imageData") openOr ""
}
