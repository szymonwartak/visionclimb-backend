package data

import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.regions.{Regions, Region}
import java.io.{FileInputStream, ByteArrayOutputStream, File}
import java.security.MessageDigest
import org.apache.commons.io.IOUtils
import org.apache.commons.codec.binary.Hex
import javax.xml.bind.annotation.adapters.HexBinaryAdapter
import org.apache.commons.codec.digest.DigestUtils

/**
 * Created with IntelliJ IDEA.
 * User: Szymon
 * Date: 27/04/2013
 * Time: 13:16
 * To change this template use File | Settings | File Templates.
 */
object S3 {
  val domain = "climage.images"

  val client = {
    val c = new AmazonS3Client()
    c.setRegion(Region.getRegion(Regions.EU_WEST_1))
    c
  }

  def putFile(file:File) = {
    val key = getKey(file)
    val result = client.putObject(domain, key, file)
    if (getFileMD5(file) != result.getETag)
      println("MD5 mismatch!")
    key
  }
  def getKey(file:File) = System.currentTimeMillis().toString+file.hashCode().toString
  val md5 = MessageDigest.getInstance("MD5");
  def getFileMD5(file:File) =
   DigestUtils.md5Hex(IOUtils.toByteArray(new FileInputStream(file)))

  putFile(new File("/Users/Szymon/me.jpg"))
}
