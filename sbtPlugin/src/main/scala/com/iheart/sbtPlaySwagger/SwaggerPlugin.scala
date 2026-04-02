package com.iheart.sbtPlaySwagger

import com.typesafe.sbt.packager.archetypes.JavaAppPackaging
import com.typesafe.sbt.packager.universal.UniversalPlugin.autoImport.*
import com.typesafe.sbt.web.Import.*
import sbt.Attributed.*
import sbt.Keys.*
import sbt.{AutoPlugin, *}

object SwaggerPlugin extends AutoPlugin {

  private val faviconRef: String =
    "data:image/png;base64,iVBORw0KGgoAAAANSUhEUgAAACAAAAAgCAMAAABEpIrGAAAABGdBTUEAALGPC/xhBQAAACBjSFJNAAB6JgAAgIQAAPoAAACA6AAAdTAAAOpgAAA6mAAAF3CculE8AAABy1BMVEUAAAB3M7twLb9yLcBxLr9xLr9xLr5xLr9xLr9xLb5tN7ZzLb9wL79xLr9xLb5vLr5mM7NxL75xLr9xLr+AIL9yL7xxLr9xLsB2MbpwLr1xLr9xLr9yK75wM8JxL79xOcZwLcByLb5xLr9xLr9xLsB2J8RyL79yLr9xL8BxLr9xLsBxLsBxLr9yLb9xLr9xLr5wLb9xLr9yLcB0LrlxLr9qK79xK79xLr90LsFyL8BxLsCAK6pyLsBxL8BtJLZvLLxxLr9wLsBxLcFxLsBuLr9zM79xLr9xLr91K79wLr9wLr9yL79wML9wLr5xLr9xLsBwLr9xLr99P8TRu+r38/vl2fOXZ9DSvev////7+f2RXs707/qUYs/Zxu7+/f7TvuuhddV8PsSKU8r28fv9/f7g0fGxjNyDSMfOt+n+/v/59vy0kd1zMcDp3/XNtulyML+ARMaletZ1NcG6meDn3PTLs+h6PMO1k97ezvDf0PFyL7/49fy4lt+QXM36+P3t5Peqgtmzj93l2PP9/P6rg9mCR8e4l9/p3vWVYs9+QcWuh9rh1PJzMMDm2/T59/yYZ9G0kN3v5/ju5vfEqOR5OsN6O8O9nuHo3fWJUsrzxHzPAAAAUXRSTlMAD1udzOv9y5xaDiif9p4nCo77jAgm1MkaMurkLxnQCY2C9Jf1DVebmcjp+fjKx5qYWFUL8wwk+iGJhQbBxQcX4uUt4SwUwMYYf4SUIJbolVT2+3Q9AAAAAWJLR0RY7bXEjgAAAAlwSFlzAAAAbwAAAG8B8aLcQwAAAAd0SU1FB+YMDwoIG95xaVoAAAGXSURBVDjLdZP3W8IwGITjQEVFFHFvwQVOwK3gxq3BVcU9cSvuiXvv/edKkyYtpdwvvd69bfqkXwCg8vMPCJQFBcmCQ+ShwFth4QpIpYhQiurIKBX0kCpaLexjYqGX4uL5PiERSigpmT4v2buJFNyrU6EPBaYhIB36VAbbK7nvt/X1DwyKgEx2kQjuZohhmOEREaEBIFTLebsbYOyjnoA2C8iJH2MBZnxickpIZIMcYqdnGKzZuXmeyQXB1C84FjmEWVpe4cI8IBO8b2SVEMzaOo50QC9cccNOiU2c5FPAubW9swv3KLBPAG4J5wEbHtpmCXBElijA5hilJ3CfAKc4LwRF2LhQegbPCXCB82JQgq6Hlyi9gtfjHHCDAQMwmpC5RekdhDu4t9/jv1UKuK18YNPHJwinnln38ko2EoCycmTf3j8+v1jz/fPrcvzhXl/BDkSl74GpQhNVXeOrrzXjobTUSff1dPAbJInGJv5gNLd499ZW4dEyt5k86/x2s+h0WjoEiEnTKXG+jYYuq06h6Lb29Br59B9jKQitCx+kXQAAACV0RVh0ZGF0ZTpjcmVhdGUAMjAyMi0xMi0xNVQxMDowODoyNyswMDowMMe6HOEAAAAldEVYdGRhdGU6bW9kaWZ5ADIwMjItMTItMTVUMTA6MDg6MjcrMDA6MDC256RdAAAAGXRFWHRTb2Z0d2FyZQB3d3cuaW5rc2NhcGUub3Jnm+48GgAAAFd6VFh0UmF3IHByb2ZpbGUgdHlwZSBpcHRjAAB4nOPyDAhxVigoyk/LzEnlUgADIwsuYwsTIxNLkxQDEyBEgDTDZAMjs1Qgy9jUyMTMxBzEB8uASKBKLgDqFxF08kI1lQAAAABJRU5ErkJggg=="

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
             |  <link rel="icon" type="image/png" href="$faviconRef">
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
       |  <link rel="icon" type="image/png" href="$faviconRef">
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
