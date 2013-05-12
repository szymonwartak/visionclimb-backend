import sbt._
import Keys._
import play.Project._
import com.github.play2war.plugin._

object ApplicationBuild extends Build {

    val appName         = "backend"
    val appVersion      = "1.0"

    val appDependencies = Seq(
//      "se.radley" %% "play-plugins-salat" % "1.2",
//      "org.mongodb" %% "casbah" % "2.5.0",
//      "com.novus" %% "salat" % "1.9.2-SNAPSHOT",
      "com.amazonaws" % "aws-java-sdk" % "1.4.1",
      "commons-io" % "commons-io" % "2.4",

  //      "org.apache.httpcomponents" % "httpclient" % "4.2.3",

  "com.lambdaworks" %% "jacks" % "2.1.4"
    )

    val main = play.Project(appName, appVersion, appDependencies)
      .settings(Play2WarPlugin.play2WarSettings: _*)
      .settings(
      Play2WarKeys.servletVersion := "3.0",
      watchSources += new File("/Users/Szymon/mutina/dev/backend/public/index.html"), //baseDirectory(_ / "public"),
      resolvers ++= Seq(
        "Java.net Maven2 Repository" at "http://download.java.net/maven/2/",
        "Sonatype scala-tools repo" at "https://oss.sonatype.org/content/groups/scala-tools/",
        "Sonatype OSS Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots"
      )
//      routesImport += "se.radley.plugin.salat.Binders._",
//      templatesImport += "org.bson.types.ObjectId"
    )

}
