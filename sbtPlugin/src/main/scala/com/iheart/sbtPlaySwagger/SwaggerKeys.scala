package com.iheart.sbtPlaySwagger

import java.io.File

import sbt.{SettingKey, TaskKey}

trait SwaggerKeys {
  val swagger: TaskKey[File] = TaskKey[File]("swagger", "generates the swagger API documentation")
  val swaggerDomainNameSpaces: SettingKey[Seq[String]] =
    SettingKey[Seq[String]]("swaggerDomainNameSpaces", "swagger domain namespaces for model classes")
  val swaggerTarget: SettingKey[File] =
    SettingKey[File]("swaggerTarget", "the location of the swagger documentation in your packaged app.")
  val swaggerFileName: SettingKey[String] =
    SettingKey[String]("swaggerFileName", "the swagger filename the swagger documentation in your packaged app.")
  val swaggerRoutesFile: SettingKey[String] =
    SettingKey[String]("swaggerRoutesFile", "the root routes file with which play-swagger start to parse")
  val swaggerOutputTransformers: SettingKey[Seq[String]] =
    SettingKey[Seq[String]]("swaggerOutputTransformers", "list of output transformers for processing swagger file")
  val swaggerV3: SettingKey[Boolean] =
    SettingKey[Boolean]("swaggerV3", "whether to to produce output compatible with Swagger 3 (also knwon as OpenAPI 3)")
  val envOutputTransformer = "com.iheart.playSwagger.EnvironmentVariablesTransformer"

  val swaggerAPIVersion: SettingKey[String] = SettingKey[String]("swaggerAPIVersion", "Version of the API")

  val swaggerPrettyJson: SettingKey[Boolean] =
    SettingKey[Boolean]("swaggerPrettyJson", "True, if needs to pretty print Swagger's documentation")

  val swaggerPlayJava: SettingKey[Boolean] =
    SettingKey[Boolean]("swaggerPlayJava", "True, if use play java and use jackson to generate model schema")

  val swaggerNamingStrategy: SettingKey[String] =
    SettingKey[String]("swaggerNamingStrategy", "Naming strategy to decode case class fields")

  val swaggerOperationIdNamingFully: SettingKey[Boolean] =
    SettingKey[Boolean](
      "swaggerOperationIdNaming",
      "Either use the operationId of the generated json as the method name"
    )

  val embedScaladoc: SettingKey[Boolean] =
    SettingKey[Boolean](
      "embedScaladoc",
      "Output schema description using scaladoc of case class"
    )

  val swaggerGenerateHtml: SettingKey[Boolean] =
    SettingKey[Boolean]("swaggerGenerateHtml", "Generate an index.html viewer alongside swagger.json")

  val swaggerHtmlRenderer: SettingKey[String] =
    SettingKey[String]("swaggerHtmlRenderer", "HTML renderer to use: \"scalar\" (default) or \"redoc\"")

  val swaggerHtmlRenderers: SettingKey[Seq[String]] =
    SettingKey[Seq[String]](
      "swaggerHtmlRenderers",
      "HTML renderers to generate. Overrides swaggerHtmlRenderer when non-empty."
    )

  val swaggerHideDownloadButton: SettingKey[Boolean] =
    SettingKey[Boolean](
      "swaggerHideDownloadButton",
      "Hide the download spec button in the HTML viewer "
    )

  val swaggerHideTryIt: SettingKey[Boolean] =
    SettingKey[Boolean]("swaggerHideTryIt", "Hide the Try it if supported")

  val swaggerScalarVersion: SettingKey[String] =
    SettingKey[String]("swaggerScalarVersion", "Pinned @scalar/api-reference CDN version (e.g. \"1.49.5\")")

  val swaggerScalarLayout: SettingKey[String] =
    SettingKey[String]("swaggerScalarLayout", "Scalar layout: \"modern\" (default) or \"classic\"")

  val swaggerScalarDefaultOpenFirstTag: SettingKey[Boolean] =
    SettingKey[Boolean]("swaggerScalarDefaultOpenFirstTag", "Auto-expand the first tag/section when Scalar loads")

  val swaggerScalarHideDarkModeToggle: SettingKey[Boolean] =
    SettingKey[Boolean]("swaggerScalarHideDarkModeToggle", "Hide the dark/light mode toggle in Scalar")
}
