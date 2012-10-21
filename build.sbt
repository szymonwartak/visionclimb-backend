
name := "climage"

organization := "code"

version := "0.1-SNAPSHOT"

scalaVersion := "2.9.1"

net.virtualvoid.sbt.graph.Plugin.graphSettings

seq(webSettings :_*)

libraryDependencies += "org.mortbay.jetty" % "jetty" % "6.1.22" % "container"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

resolvers += "Sonatype scala-tools repo" at "https://oss.sonatype.org/content/groups/scala-tools/"

resolvers += "Twitter's Repository" at "http://maven.twttr.com/"

libraryDependencies ++= {
  val liftVersion = "2.4"
  val cassieVersion = "0.22.1"
  var finagleVersion = "5.1.0"
  Seq(
    "com.twitter" % "finagle-core" % finagleVersion excludeAll(ExclusionRule(organization = "org.slf4j")),
    "com.twitter" % "finagle-thrift" % finagleVersion excludeAll(ExclusionRule(organization = "org.slf4j")),
    "com.twitter" % "finagle-ostrich4" % finagleVersion excludeAll(ExclusionRule(organization = "org.slf4j")),
    "com.twitter" % "cassie-core" % cassieVersion excludeAll(ExclusionRule(organization = "org.slf4j")),
    "com.twitter" % "cassie-hadoop" % cassieVersion excludeAll(ExclusionRule(organization = "org.slf4j"),ExclusionRule(organization = "tomcat")),
    "com.twitter" % "cassie-serversets" % cassieVersion excludeAll(ExclusionRule(organization = "org.slf4j")),
    "com.twitter" % "cassie-stress" % cassieVersion excludeAll(ExclusionRule(organization = "org.slf4j")),
    "thrift" % "libthrift" % "0.5.0" from "http://maven.twttr.com/thrift/libthrift/0.5.0/libthrift-0.5.0.jar" excludeAll(ExclusionRule(organization = "org.slf4j")),
    "net.liftweb" %% "lift-webkit" % liftVersion % "compile" excludeAll(ExclusionRule(organization = "org.slf4j")),
    "net.liftweb" %% "lift-mapper" % liftVersion % "compile" excludeAll(ExclusionRule(organization = "org.slf4j")),
    "org.mortbay.jetty" % "jetty" % "6.1.26" % "test" excludeAll(ExclusionRule(organization = "org.slf4j")),
    //"junit" % "junit" % "4.7" % "test" excludeAll(ExclusionRule(organization = "org.slf4j")),
    "org.scalatest" %% "scalatest" % "1.8" % "test" excludeAll(ExclusionRule(organization = "org.slf4j")),
    "ch.qos.logback" % "logback-core" % "1.0.6" exclude("org.slf4j","slf4j-jdk14"),
    "ch.qos.logback" % "logback-classic" % "1.0.6" exclude("org.slf4j","slf4j-jdk14")
  )
}

