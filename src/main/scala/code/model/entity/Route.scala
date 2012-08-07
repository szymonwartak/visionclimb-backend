package code.model.entity

object Route {
	val columnSet = new java.util.HashSet[String] {
		add("routeId"); add("name")
	}
	val fullColumnSet = new java.util.HashSet[String] {
		add("routeId");
		add("name");
		add("routePointsX");
		add("routePointsY");
		add("latitude");
		add("longitude");
		add("imageId");
	}
	val areaColumnSet = new java.util.HashSet[String] {
		add("routeId");
		add("name");
		add("latitude");
		add("longitude");
		add("imageId");
	}
	val imageColumnSet = new java.util.HashSet[String] {
		add("imageId"); add("image")
	}
}

class Route() {
}
