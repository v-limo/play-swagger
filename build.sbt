ThisBuild / version := "4.1.3"
Publish.lintSettings
ThisBuild / publish / skip := true
ThisBuild / scalafixDependencies ++= Seq(
  "com.github.xuwei-k" %% "scalafix-rules" % "0.6.24",
  "net.pixiv" %% "scalafix-pixiv-rule" % "4.5.3"
)
lazy val scalaV = "3.8.1"

lazy val root = project.in(file("."))
  .aggregate(playSwagger, sbtPlaySwagger)
  .settings(
    publish / skip := true,
    sourcesInBase := false,
    scalaVersion := scalaV
  )

lazy val playSwagger = project.in(file("core"))
  .settings(
    publish / skip := false,
    Publish.coreSettings,
    name := "play-swagger",
    libraryDependencies ++= Dependencies.playTest ++
      Dependencies.playRoutesCompiler ++
      Dependencies.playJson ++
      Dependencies.enumeratum ++
      Dependencies.refined ++
      Dependencies.test ++
      Dependencies.yaml ++ Seq(
        "com.github.takezoe" %% "runtime-scaladoc-reader" % "1.1.0",
        "org.scalameta" %% "scalameta" % "4.12.7",
        "net.steppschuh.markdowngenerator" % "markdowngenerator" % "1.3.1.1",
        "joda-time" % "joda-time" % "2.12.7" % Test,
        "com.google.errorprone" % "error_prone_annotations" % "2.42.0" % Test,
        "org.scala-lang" %% "scala3-staging" % scalaVersion.value
      ),
    addCompilerPlugin("com.github.takezoe" %% "runtime-scaladoc-reader" % "1.1.0"),
    scalaVersion := scalaV,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= Seq(
      "-Wunused:all",
      "-Yretain-trees",
      "-deprecation",
      "-feature"
    )
  )

lazy val sbtPlaySwagger = project.in(file("sbtPlugin"))
  .settings(
    publish / skip := false,
    Publish.coreSettings,
    addSbtPlugin("com.github.sbt" % "sbt-native-packager" % "1.10.0" % Provided),
    addSbtPlugin("com.github.sbt" %% "sbt-web" % "1.5.8" % Provided)
  )
  .enablePlugins(BuildInfoPlugin, SbtPlugin)
  .settings(
    buildInfoKeys := Seq[BuildInfoKey](name, version),
    buildInfoPackage := "com.iheart.playSwagger",
    name := "sbt-play-swagger",
    description := "sbt plugin for play swagger spec generation",
    sbtPlugin := true,
    scalaVersion := "2.12.20", // SBT 1.x plugins must be compiled with Scala 2.12
    scriptedLaunchOpts := {
      scriptedLaunchOpts.value ++
        Seq("-Xmx1024M", "-Dplugin.version=" + version.value)
    },
    scriptedBufferLog := false,
    semanticdbEnabled := true,
    semanticdbVersion := scalafixSemanticdb.revision,
    scalacOptions ++= Seq(
      "-Xlint:unused",
      "-deprecation",
      "-feature",
      "-Ypatmat-exhaust-depth",
      "40",
      "-P:semanticdb:synthetics:on"
    )
  )
