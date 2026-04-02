package com.iheart.playSwagger

import scala.util.Try

import play.api.libs.json.*

/**
  * strips operations tagged `internal` from the generated spec.
  *   tags: [internal]
  *   GET /admin/reindex ...
  */

class FilterInternalEndpointsTransformer extends OutputTransformer {

  protected val internalTag: String = "internal"

  private val httpMethods = Set("get", "post", "put", "delete", "patch", "head", "options")

  private def isInternal(op: JsObject)
      : Boolean = (op \ "tags").asOpt[Seq[String]].getOrElse(Seq.empty).contains(internalTag)

  override def apply(json: JsObject): Try[JsObject] = Try {
    val paths = (json \ "paths").asOpt[JsObject].getOrElse(Json.obj())

    val filteredPaths = JsObject(paths.fields.flatMap {
      case (path, item: JsObject) =>
        val filteredFields = item.fields.filter {
          case (method, op: JsObject) if httpMethods.contains(method) => !isInternal(op)
          case _ => true
        }
        val hasRemainingOps = filteredFields.exists { case (m, _) => httpMethods.contains(m) }
        if (hasRemainingOps) Some(path -> JsObject(filteredFields))
        else None

      case other => Some(other)
    })

    json + ("paths" -> filteredPaths)
  }
}
