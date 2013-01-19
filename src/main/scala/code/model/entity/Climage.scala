package code.model.entity

/**
 * Created with IntelliJ IDEA.
 * User: szymon
 * Date: 13/01/13
 * Time: 00:48
 * To change this template use File | Settings | File Templates.
 */
class Climage {

}

object Climage {

	val columnSet = new java.util.HashSet[String] {
		add("climageId")
		add("latitude")
		add("longitude")
		add("name")
	}

	val fullColumnSet = new java.util.HashSet[String] {
		add("climageId")
		add("latitude")
		add("longitude")
		add("name")
		add("imageData")
	}


}