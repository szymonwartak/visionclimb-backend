package controllers

import _root_.util.Logging
import data._

import play.api.mvc._
import play.api.libs.json._
import org.apache.commons.codec.binary.Base64
import com.lambdaworks.jacks.JacksMapper


object Application extends Controller with Logging {

  def postLog(userId:String) = Action(parse.urlFormEncoded) { request =>
    log.debug(request.body.getOrElse("message", Set("0")).head)
    Ok("logged")
  }
  def getAreas(userId:String) = Action {
    log.debug("CALL:getAreas")
    val result = Area.scan()
    log.debug("getAreas:"+result)
    Ok(JacksMapper.writeValueAsString(result))
  }

  def postArea = Action(parse.urlFormEncoded) { request =>
    log.debug("CALL:postArea")
    val nextAreaId = Dynamo.incrementCounter("area", "count")
    log.debug("postArea:"+nextAreaId)
    // TODO: reject areas with no lat/lng
    val area = Area(nextAreaId,
      request.body.getOrElse("latitude", Set("0")).head,
      request.body.getOrElse("longitude", Set("0")).head,
      request.body.getOrElse("name", Set("-")).head
    )
    area.write()
    log.debug("area written:"+area)
    Ok(JacksMapper.writeValueAsString(area))
  }
	def postAreaJson = Action(parse.json) { request =>
		request.body.validate(Json.reads[Area]).map { area =>
			log.debug("postArea: "+area)
      area.write()
			Ok("ok")
		}.get
	}
  def getAreaClimages(areaId:String, userId:String) = Action {
    val climages = Climage.getAreaClimages(areaId)
    log.debug("getAreaClimages:"+climages)
    climages.foreach {climage =>
      log.debug("routes (%s):".format(climage.climageId)+JacksMapper.writeValueAsString(climage.routes))
      log.debug("climage:"+JacksMapper.writeValueAsString(climage))
    }
    log.debug("allJSON:"+JacksMapper.writeValueAsString(climages))
    Ok(JacksMapper.writeValueAsString(climages))
  }
  def getClimage(climageId:String, userId:String) = Action {
    val json = Climage.getClimage(climageId) match {
      case Some(climage) => JacksMapper.writeValueAsString(climage)
      case None => "{}"
    }
    log.debug("getClimage:"+json)
    Ok(json)
  }
  val base64Decoder = new Base64();
  def uploadImage = Action(parse.urlFormEncoded(1024*1024)) { request =>
    val data = request.body.getOrElse("imageData",Set("")).head
    S3.putFile(data)
    Ok("ok")
  }
  def postRouteWithImage = Action(parse.urlFormEncoded(1024*1024)) { request =>
    log.debug("CALL-postRouteWithImage")
    // TODO: climage with no lat/lng defaults to area info
    val imageData = request.body.getOrElse("imageData",Set("")).head
    val s3ImageId = S3.putFile(imageData)
    log.debug("imageID:"+s3ImageId)

    val areaId = request.body.getOrElse("areaId", Set("")).head
    log.debug("areaId:"+areaId)
    val nextClimageId = "%s_%s".format(areaId, Dynamo.incrementCounter(areaId, "count"))
    log.debug("climageID:"+nextClimageId)
    val nextRouteId = "%s_%s".format(nextClimageId, Dynamo.incrementCounter(nextClimageId, "count"))
    log.debug("area:%s climage:%s route:%s s3id:%s".format(areaId, nextClimageId, nextRouteId, s3ImageId))
    val climage = Climage(nextClimageId,
      request.body.getOrElse("latitude",Set("")).head,
      request.body.getOrElse("longitude",Set("")).head,
      request.body.getOrElse("name",Set("")).head,
      s3ImageId
    )
    log.debug("climage:"+climage)
    climage.writeJson()
    log.debug("climage written")
    val route = Route(nextRouteId,
      request.body.getOrElse("name",Set("")).head,
      request.body.getOrElse("grade",Set("")).head,
      request.body.getOrElse("routePointsX",Set("")).head,
      request.body.getOrElse("routePointsY",Set("")).head
    )
    log.debug(route.toString)
    route.writeJson()
    log.debug("route written")

    Ok(Json.toJson(Map("routeId"->nextRouteId, "climageId"->nextClimageId, "imageId"->s3ImageId)))
//    Ok("""{"routeId":%s,"climageId":%s,"imageId":%s}""".format(nextRouteId, nextClimageId, s3ImageId))
  }
  def postRoute = Action(parse.urlFormEncoded) { request =>
    val climageId = request.body.getOrElse("climageId", Set("0")).head
    val nextRouteId = "%s_%s"format(climageId, Dynamo.incrementCounter(climageId, "count"))
    val route = Route(nextRouteId,
      request.body.getOrElse("name", Set("0")).head,
      request.body.getOrElse("grade", Set("0")).head,
      request.body.getOrElse("routePointsX", Set("0")).head,
      request.body.getOrElse("routePointsY", Set("0")).head
    )
    route.writeJson()
    Ok(Json.toJson(Map("routeId"->nextRouteId, "climageId"->climageId)))
//    Ok("""{"routeId":%s,"climageId":%s}""".format(nextRouteId, climageId))
  }

}

