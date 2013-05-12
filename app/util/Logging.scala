package util

import org.slf4j.LoggerFactory

/**
 * Created with IntelliJ IDEA.
 * User: Szymon
 * Date: 12/05/2013
 * Time: 21:08
 * To change this template use File | Settings | File Templates.
 */
trait Logging {
  val log = LoggerFactory.getLogger("main")
}
