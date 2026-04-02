import aether.AetherKeys.*
import sbt.Keys.*
import sbt.{Def, *}

object Publish {

  val coreSettings: Seq[Def.Setting[?]] = Seq(
    organization := "io.github.play-swagger",
    licenses := Seq("Apache-2.0" ->
      url("https://www.apache.org/licenses/LICENSE-2.0.html")),
    homepage :=
      Some(url("https://bitbucket.org/canteroy/adeona-play-swagger")),
    scmInfo := Some(ScmInfo(
      url("https://bitbucket.org/canteroy/adeona-play-swagger"),
      "scm:git:git@bitbucket.org:canteroy/adeona-play-swagger.git"
    )),
    developers := List(),
    credentials += {
      sys.env.get("PACKAGECLOUD_TOKEN") match {
        case Some(token) => Credentials(
            "packagecloud",
            "packagecloud.io",
            "",
            token
          )
        case None => Credentials(Path.userHome / ".ivy2" /
            ".credentials")
      }
    },
    aetherWagons := Seq(
      aether.WagonWrapper(
        "packagecloud+https",
        "io.packagecloud.maven.wagon.PackagecloudWagon"
      )
    ),
    publishTo := Some("packagecloud+https" at
      "packagecloud+https://packagecloud.io/canter/releases")
  )

  // excludeLintKeys must be set globally, not per-project
  val lintSettings: Seq[Def.Setting[?]] = Seq(
    Global / excludeLintKeys += aetherWagons
  )

}
