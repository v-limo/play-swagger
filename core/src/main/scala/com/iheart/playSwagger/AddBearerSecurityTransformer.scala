package com.iheart.playSwagger

import scala.util.Try

import play.api.libs.json.*

/**
  * Adds Bearer JWT security to the generated spec.
  * To mark an endpoint as not requiring auth, tag it `public` in the routes file:
  * tags: [public]
  */
class AddBearerSecurityTransformer extends OutputTransformer {

  protected val publicTag: String =
    Option(System.getenv("SWAGGER_PUBLIC_TAG")).filter(_.nonEmpty).getOrElse("public")

  private val httpMethods = Set("get", "post", "put", "delete", "patch", "head", "options")

  private val bearerSecurity = Json.arr(Json.obj("BearerAuth" -> Json.arr()))

  private def withSecurity(op: JsObject): JsObject = {
    val tags = (op \ "tags").asOpt[Seq[String]].getOrElse(Seq.empty)
    if (tags.contains(publicTag)) op
    else op + ("security" -> bearerSecurity)
  }

  private def applyToPath(pathItem: JsObject): JsObject =
    JsObject(pathItem.fields.map {
      case (method, op: JsObject) if httpMethods.contains(method) => method -> withSecurity(op)
      case other => other
    })

  private def applyToPaths(json: JsObject): JsObject = {
    val paths = (json \ "paths").asOpt[JsObject].getOrElse(Json.obj())
    json +
      ("paths" -> JsObject(paths.fields.map {
        case (path, item: JsObject) => path -> applyToPath(item)
        case other => other
      }))
  }

  override def apply(json: JsObject): Try[JsObject] = Try {
    val isV3 = (json \ "openapi").isDefined

    if (isV3) {
      val components = (json \ "components").asOpt[JsObject].getOrElse(Json.obj())
      val existingSchemes = (components \ "securitySchemes").asOpt[JsObject].getOrElse(Json.obj())
      val bearerScheme = Json.obj(
        "BearerAuth" -> Json.obj("type" -> "http", "scheme" -> "bearer", "bearerFormat" -> "JWT")
      )
      val updatedComponents = components + ("securitySchemes" -> (existingSchemes ++ bearerScheme))
      applyToPaths(json + ("components" -> updatedComponents))
    } else {
      val existing = (json \ "securityDefinitions").asOpt[JsObject].getOrElse(Json.obj())
      val bearerDef = Json.obj(
        "BearerAuth" -> Json.obj("type" -> "apiKey", "in" -> "header", "name" -> "Authorization")
      )
      applyToPaths(json + ("securityDefinitions" -> (existing ++ bearerDef)))
    }
  }
}
