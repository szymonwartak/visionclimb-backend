import sbt._

class LiftProject(info: ProjectInfo) extends DefaultWebProject(info) {
  val liftVersion = property[Version]
  val cassieVersion = "0.22.1"
  var finagleVersion = "5.1.0"

  // uncomment the following if you want to use the snapshot repo
  //  val scalatoolsSnapshot = ScalaToolsSnapshots

  // If you're using JRebel for Lift development, uncomment
  // this line
  // override def scanDirectories = Nil

  lazy val JavaNet = "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
  //val JavaRepo = "Java.net Repository for Maven" at "http://download.java.net/maven/2/"
  val SonatypeRep = "Sonatype scala-tools repo" at "https://oss.sonatype.org/content/groups/scala-tools/"
  val twttr = "Twitter's Repository" at "http://maven.twttr.com/"

  //resolvers += JavaNet + SonatypeRep + twttr

  override def libraryDependencies = Set(
    //"com.twitter" % "cassie" % "0.20.0",
    "com.twitter" % "finagle-core" % finagleVersion,
    "com.twitter" % "finagle-thrift" % finagleVersion,
    "com.twitter" % "finagle-ostrich4" % finagleVersion,
    "com.twitter" % "cassie-core" % cassieVersion,
    "com.twitter" % "cassie-hadoop" % cassieVersion,
    "com.twitter" % "cassie-serversets" % cassieVersion,
    "com.twitter" % "cassie-stress" % cassieVersion,
    "thrift" % "libthrift" % "0.5.0" from "http://maven.twttr.com/thrift/libthrift/0.5.0/libthrift-0.5.0.jar",
    "net.liftweb" %% "lift-webkit" % liftVersion.value.toString % "compile",
    "net.liftweb" %% "lift-mapper" % liftVersion.value.toString % "compile",
    "org.mortbay.jetty" % "jetty" % "6.1.26" % "test",
    "junit" % "junit" % "4.7" % "test",
    "ch.qos.logback" % "logback-classic" % "0.9.26",
    "org.scala-tools.testing" %% "specs" % "1.6.8" % "test",
    "com.h2database" % "h2" % "1.2.147"
  ) ++ super.libraryDependencies
}
