package code.model.entity

object Route {
	val fullColumnSet = new java.util.HashSet[String] {
		add("routeId")
		add("name")
		add("grade")
		add("routePointsX")
		add("routePointsY")
	}

}

class Route() {
}
