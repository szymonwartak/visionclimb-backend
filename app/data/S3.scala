package data

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.regions.{Regions, Region}
import java.io._
import java.security.MessageDigest
import org.apache.commons.codec.binary.Base64
import org.apache.commons.codec.digest.DigestUtils
import com.amazonaws.services.s3.model.{CannedAccessControlList, PutObjectRequest}
import util.Logging

/**
 * Created with IntelliJ IDEA.
 * User: Szymon
 * Date: 27/04/2013
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
object S3 extends Logging {
  val domain = "climage.images"

  val client = {
    val c = new AmazonS3Client()
    c.setRegion(Region.getRegion(Regions.EU_WEST_1))
    c
  }

  def putFile(dataStr:String) = {
    log.debug("CALL:putFile")
    val data = dataStr.substring(dataStr.indexOf("base64,")+7,dataStr.size-1)
    val imgBytes = new Base64().decode(data.getBytes("UTF-8"))
    val key = getKey(imgBytes)
    log.debug("key: "+key)
    val filename = "/tmp/%s.png".format(key)
    val osf = new FileOutputStream(filename)
    osf.write(imgBytes); osf.flush()
    log.debug("file written:"+filename)

    val result = client.putObject(new PutObjectRequest(domain, key, new File(filename)).withCannedAcl(CannedAccessControlList.PublicRead))
    log.debug("uploaded to S3")
//    if (getFileMD5(data) != result.getETag)
//      println("MD5 mismatch!")
    key
  }
  def getKey(data:Array[Byte]) = System.currentTimeMillis().toString+data.hashCode().toString
  val md5 = MessageDigest.getInstance("MD5");
  def getFileMD5(data:Array[Byte]) =
   DigestUtils.md5Hex(data)

}

