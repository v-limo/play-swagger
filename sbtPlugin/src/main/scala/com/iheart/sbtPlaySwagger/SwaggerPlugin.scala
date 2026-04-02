package com.iheart.sbtPlaySwagger

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.*
import com.typesafe.sbt.web.Import.*
import sbt.Attributed.*
import sbt.Keys.*
import sbt.{AutoPlugin, *}

object SwaggerPlugin extends AutoPlugin {

  private val faviconRef: String =
    "data:image/svg+xml,%3Csvg xmlns='http://www.w3.org/2000/svg' viewBox='0 0 1080 1080'%3E%3Ccircle fill='%23712ebf' cx='540' cy='539.72' r='540'/%3E%3Cpath fill='%23fff' d='M500.94,580.52c-12.02-33.81-28.23-64.89-57.12-87.98-33.44-26.72-80.24-36.09-109.28-67.05-36.75-39.18-42.08-98.62-1.08-132.36,24.78-20.4,57.93-27.6,88.68-18.31,36.82,11.13,51.62,41.27,75.38,68.01,40.52,45.6,104.24,52.75,159.41,69.03,57.48,16.96,117.51,41.08,154.41,90.79,70.36,94.77,22.05,265-92.95,299.55-59.09,17.75-116.56-6.17-153.14-54.3-35.93-47.28-44.32-111.13-64.32-167.38Z'/%3E%3Cpath fill='%23fff' d='M396.92,793.27c-22.61,12.57-49.95,17.98-74.51,14.12-29.41-4.62-53.85-22.92-69.32-48.01-6.99-11.34-11.98-24.79-14.37-40.41-8.13-37.83,1.64-76.89,21.76-108.43,4.02-6.31,8.46-12.32,13.25-17.95,3.96-4.66,8.2-9.12,12.69-13.3,28.09-23.96,71.91-51.58,110.84-23.96,38.93,27.62,52.66,60.18,55.65,105.18,.71,6.16,1.13,12.46,1.23,18.81,.5,31.77-7.08,64.96-27.54,89.7-8.05,9.73-18.26,17.9-29.68,24.25Z'/%3E%3C/svg%3E"

  // uses built-in "purple" as base, then overrides with exact pim-ui palette via CSS vars
  //   sidebar bg  = $pim-dark   #1d0c31
  //   accent      = $pim-purple #712ebf
  //   font        = Sofia Sans Semi Condensed
  private val scalarCss: String =
    """
			|        :root {
			|          --scalar-sidebar-background-1: #1d0c31;
			|          --scalar-sidebar-color-1: #f0ebfa;
			|          --scalar-sidebar-color-active: #ffffff;
			|          --scalar-color-accent: #712ebf;
			|          --scalar-font: 'Sofia Sans Semi Condensed', sans-serif;
			|          --scalar-font-code: 'JetBrains Mono', 'Fira Code', monospace;
			|        }
            |        .group\\/sidebar-section > ul {                  
            |           padding-left: 1rem;
            |          }
            |         .scalar-card:has(.servers) {                                                                                                                                                                                                                               
            |           display: none;                                                                                                                                                                                                                                           
            |          }
            |         [data-sidebar-id$="/description/introduction"] > .group\\/group-button .group\\/button-label,
            |         [data-sidebar-id$="/models"] > .group\\/group-button .group\\/button-label {
            |          text-transform: uppercase;
            |          font-weight: 600;
            |          font-size: 0.7em;
            |          letter-spacing: 0.05em;
            |          color: var(--scalar-sidebar-color-1);
            |        }
			|      """.stripMargin

  private def scalarHtml(
      specUrl: String,
      version: String,
      layout: String,
      hideDownloadButton: Boolean,
      hideTryIt: Boolean,
      defaultOpenFirstTag: Boolean,
      hideDarkModeToggle: Boolean
  ): String =
    s"""<!doctype html>
			 |<html>
			 |<head>
			 |  <title>Adeona API Reference </title>
			 |  <meta charset="utf-8" />
			 |  <meta name="viewport" content="width=device-width, initial-scale=1" />
             |  <link rel="icon" type="image/svg+xml" href="$faviconRef">
			 |</head>
			 |<body>
			 |  <div id="app"></div>
			 |  <script src="https://cdn.jsdelivr.net/npm/@scalar/api-reference@$version"></script>
			 |  <script>
			 |    Scalar.createApiReference('#app', {
			 |      url: '$specUrl',
			 |      theme: 'purple',
			 |      layout: '$layout',
			 |      withDefaultFonts: false,
			 |      hideDownloadButton: $hideDownloadButton,
			 |      hideTestRequestButton: $hideTryIt,
			 |      hideClientButton: $hideTryIt,
			 |      defaultOpenFirstTag: $defaultOpenFirstTag,
			 |      hideDarkModeToggle: $hideDarkModeToggle,
			 |      operationTitleSource: 'summary',
			 |      showOperationId: false,
			 |      orderRequiredPropertiesFirst: true,
			 |      telemetry: false,
			 |      persistAuth: true,
			 |      customCss: `$scalarCss`
			 |    })
			 |  </script>
			 |</body>
			 |</html>""".stripMargin

  // Redoc is always read-only — no try-it
  private def redocHtml(specUrl: String, hideDownloadButton: Boolean): String =
    s"""<!DOCTYPE html>                                                                                                                                                                                                                                      
       |<html>                                                                                                                                                                                                                                               
       |<head>                                                                                                                                                                                                                                               
       |  <title>Adeona PIM API Reference</title>                                                                                                                                                                                                            
       |  <meta charset="utf-8"/>                                                                                                                                                                                                                            
       |  <meta name="viewport" content="width=device-width, initial-scale=1">
       |  <link href="https://fonts.googleapis.com/css2?family=Sofia+Sans+Semi+Condensed:wght@400;600;700&display=swap" rel="stylesheet">
       |  <link rel="icon" type="image/svg+xml" href="$faviconRef">
       |  <style>body { margin: 0; padding: 0; }</style>
       |</head>                                                                                                                                                                                                                                              
       |<body>                                                                                                                                                                                                                                               
       |  <div id="redoc-container"></div>                                                                                                                                                                                                                   
       |  <script src="https://cdn.redoc.ly/redoc/latest/bundles/redoc.standalone.js"></script>                                                                                                                                                              
       |  <script>                                                                                                                                                                                                                                           
       |    Redoc.init('$specUrl', {                                                                                                                                                                                                                         
       |      hideDownloadButton: $hideDownloadButton,                                                                                                                                                                                                       
       |      expandResponses: '200,201',                   
       |      sortRequiredPropsFirst: true,                                                                                                                                                                                                                  
       |      jsonSamplesExpandLevel: 2,                    
       |      schemasExpansionLevel: 1,                                                                                                                                                                                                                      
       |    }, document.getElementById('redoc-container'))
       |  </script>                                                                                                                                                                                                                                          
       |</body>                                             
       |</html>""".stripMargin

  private lazy val SwaggerConfig: Configuration = config("play-swagger").hide
  private lazy val playSwaggerVersion: String = com.iheart.playSwagger.BuildInfo.version

  object autoImport extends SwaggerKeys

  override def requires: Plugins = JavaAppPackaging

  override def trigger = noTrigger

  import autoImport.*

  override def projectConfigurations: Seq[Configuration] = Seq(SwaggerConfig)

  override def projectSettings: Seq[Setting[?]] = Seq(
    ivyConfigurations += SwaggerConfig,
    resolvers += Resolver.jcenterRepo,
    // todo: remove hardcoded org name using BuildInfo
    libraryDependencies += "io.github.play-swagger" %% "play-swagger" % playSwaggerVersion % SwaggerConfig,
    dependencyOverrides ++= {
      if (scalaBinaryVersion.value == "3")
        Seq(
          "org.scala-lang" %% "scala3-library" % scalaVersion.value % SwaggerConfig,
          "org.scala-lang" %% "scala3-compiler" % scalaVersion.value % SwaggerConfig,
          "org.scala-lang" %% "scala3-staging" % scalaVersion.value % SwaggerConfig
        )
      else Nil
    },
    swaggerDomainNameSpaces := Seq(),
    swaggerV3 := false,
    swaggerTarget := target.value / "swagger",
    swaggerFileName := "swagger.json",
    swaggerRoutesFile := "routes",
    swaggerOutputTransformers := Seq("com.iheart.playSwagger.ParametricTypeNamesTransformer"),
    swaggerAPIVersion := version.value,
    swaggerPrettyJson := false,
    swaggerPlayJava := false,
    swaggerNamingStrategy := "none",
    swaggerOperationIdNamingFully := false,
    embedScaladoc := false,
    swaggerGenerateHtml := false,
    swaggerHtmlRenderer := "scalar",
    swaggerHtmlRenderers := Seq(),
    swaggerHideDownloadButton := false,
    swaggerHideTryIt := false,
    swaggerScalarVersion := "1.49.5",
    swaggerScalarLayout := "modern",
    swaggerScalarDefaultOpenFirstTag := false,
    swaggerScalarHideDarkModeToggle := false,
    swagger := Def.task[File] {
      swaggerTarget.value.mkdirs()
      val file = swaggerTarget.value / swaggerFileName.value
      IO.delete(file)
      val args: Seq[String] = file.absolutePath :: swaggerRoutesFile.value ::
        swaggerDomainNameSpaces.value.mkString(",") ::
        swaggerOutputTransformers.value.mkString(",") ::
        swaggerV3.value.toString ::
        swaggerAPIVersion.value ::
        swaggerPrettyJson.value.toString ::
        swaggerPlayJava.value.toString ::
        swaggerNamingStrategy.value ::
        swaggerOperationIdNamingFully.value.toString ::
        embedScaladoc.value.toString ::
        Nil
      val swaggerClasspath =
        data((Runtime / fullClasspath).value) ++ update.value.select(configurationFilter(SwaggerConfig.name))
      runner.value.run(
        "com.iheart.playSwagger.SwaggerSpecRunner",
        swaggerClasspath,
        args,
        streams.value.log
      ).failed foreach (sys error _.getMessage)
      if (swaggerGenerateHtml.value) {
        val specUrl = swaggerFileName.value
        val renderers =
          if (swaggerHtmlRenderers.value.nonEmpty) swaggerHtmlRenderers.value
          else Seq(swaggerHtmlRenderer.value)

        def htmlFor(renderer: String): String = renderer.toLowerCase match {
          case "redoc" => redocHtml(specUrl, swaggerHideDownloadButton.value)
          case _ =>
            scalarHtml(
              specUrl,
              swaggerScalarVersion.value,
              swaggerScalarLayout.value,
              swaggerHideDownloadButton.value,
              swaggerHideTryIt.value,
              swaggerScalarDefaultOpenFirstTag.value,
              swaggerScalarHideDarkModeToggle.value
            )
        }

        renderers.zipWithIndex.foreach { case (renderer, idx) =>
          val html = htmlFor(renderer)
          IO.write(swaggerTarget.value / s"${renderer.toLowerCase}.html", html)
          if (idx == 0) IO.write(swaggerTarget.value / "index.html", html)
        }
      }
      file
    }.value,
    Compile / packageBin / mappings ++= {
      if (swaggerGenerateHtml.value) {
        val renderers =
          if (swaggerHtmlRenderers.value.nonEmpty) swaggerHtmlRenderers.value
          else Seq(swaggerHtmlRenderer.value)
        val named =
          renderers.map(r => (swaggerTarget.value / s"${r.toLowerCase}.html") -> s"public/${r.toLowerCase}.html")
        val index = Seq((swaggerTarget.value / "index.html") -> "public/index.html")
        named ++ index
      } else Nil
    },
    Assets / unmanagedResourceDirectories += swaggerTarget.value,
    Compile / packageBin / mappings += swagger.value -> s"public/${swaggerFileName.value}", // include it in the unmanagedResourceDirectories in Assets doesn't automatically include it package
    Universal / packageBin := (Universal / packageBin).dependsOn(swagger).value,
    run := (Compile / run).dependsOn(swagger).evaluated,
    stage := stage.dependsOn(swagger).value
  )
}
