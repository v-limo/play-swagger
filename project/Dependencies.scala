import sbt.*

object Dependencies {
  object Versions {
    val play = "3.0.10"
    val playJson = "3.0.6"
    val specs2 = "4.23.0"
    val enumeratum = "1.9.6"
    val refined = "0.11.3"
  }

  val playTest: Seq[ModuleID] = Seq(
    "org.playframework" %% "play-test" % Versions.play % Test
  )

  val playRoutesCompiler: Seq[ModuleID] = Seq(
    "org.playframework" %% "play-routes-compiler" % Versions.play
  )

  val playJson: Seq[ModuleID] = Seq(
    "org.playframework" %% "play-json" % Versions.playJson % "provided"
  )

  val yaml: Seq[ModuleID] = Seq(
    "org.yaml" % "snakeyaml" % "2.6"
  )

  val enumeratum: Seq[ModuleID] = Seq(
    "com.beachape" %% "enumeratum" % Versions.enumeratum % Test
  )

  val refined: Seq[ModuleID] = Seq(
    "eu.timepit" %% "refined" % Versions.refined
  )

  val test: Seq[ModuleID] = Seq(
    "org.specs2" %% "specs2-core" % Versions.specs2 % "test"
  )
}
