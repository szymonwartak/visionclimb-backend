package code.util

import org.slf4j.LoggerFactory

trait Logging {
	val log = LoggerFactory.getLogger("app")
}