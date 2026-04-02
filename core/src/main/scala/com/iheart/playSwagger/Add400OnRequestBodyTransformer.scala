package com.iheart.playSwagger

import scala.util.Try

import play.api.libs.json.*

class Add400OnRequestBodyTransformer extends OutputTransformer {

  private val methods = Set("post", "put", "patch")

  override def apply(json: JsObject): Try[JsObject] = Try {
    val paths = (json \ "paths").asOpt[JsObject].getOrElse(Json.obj())
    val updatedPaths = JsObject(paths.fields.map {
      case (path, item: JsObject) =>
        path -> JsObject(item.fields.map {
          case (method, op: JsObject) if methods.contains(method) =>
            val hasRequiredBody = (op \ "requestBody" \ "required").asOpt[Boolean].getOrElse(false)
            if (hasRequiredBody) {
              val responses = (op \ "responses").asOpt[JsObject].getOrElse(Json.obj())
              if ((responses \ "400").isDefined) {
                method -> op
              } else {
                val badRequest = Json.obj("400" -> Json.obj("description" -> "Invalid request body"))
                method -> (op + ("responses" -> (badRequest ++ responses)))
              }
            } else method -> op
          case other => other
        })
      case other => other
    })
    json + ("paths" -> updatedPaths)
  }
}
