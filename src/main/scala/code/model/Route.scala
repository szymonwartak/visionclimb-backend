package code.model


object Route {
	val columnSet = new java.util.HashSet[String] {add("id");add("name")}
	val fullColumnSet = new java.util.HashSet[String] {add("id");add("name");add("routePointsX");add("routePointsY");add("imageId")}
	val imageColumnSet = new java.util.HashSet[String] {add("id");add("image")}

}

class Route() {
}
