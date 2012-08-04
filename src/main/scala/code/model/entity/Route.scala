package code.model.entity

object Route {
	val columnSet = new java.util.HashSet[String] {
		add("id"); add("name")
	}
	val fullColumnSet = new java.util.HashSet[String] {
		add("id");
		add("name");
		add("routePointsX");
		add("routePointsY");
		add("latitude");
		add("longitude");
		add("imageId")
	}
	val areaColumnSet = new java.util.HashSet[String] {
		add("id");
		add("name");
		add("latitude");
		add("longitude");
	}
	val imageColumnSet = new java.util.HashSet[String] {
		add("id"); add("image")
	}

}

class Route() {
}
