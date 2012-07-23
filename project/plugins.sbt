libraryDependencies <+= sbtVersion(v => v match {
case "0.11.0" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.0-0.2.8" excludeAll(ExclusionRule(organization = "org.slf4j"))
case "0.11.1" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.1-0.2.10" excludeAll(ExclusionRule(organization = "org.slf4j"))
case "0.11.2" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.2-0.2.11" excludeAll(ExclusionRule(organization = "org.slf4j"))
case "0.11.3" => "com.github.siasia" %% "xsbt-web-plugin" % "0.11.3-0.2.11.1" excludeAll(ExclusionRule(organization = "org.slf4j"))
})

addSbtPlugin("com.github.mpeltonen" % "sbt-idea" % "1.0.0" excludeAll(ExclusionRule(organization = "org.slf4j")))
