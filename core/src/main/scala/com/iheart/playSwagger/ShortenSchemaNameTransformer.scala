package com.iheart.playSwagger

import scala.util.Try

import play.api.libs.json.*

/**
  * Strips package prefixes from schema names in the generated spec
  */
class ShortenSchemaNameTransformer extends OutputTransformer {

  override def apply(json: JsObject): Try[JsObject] = Try {
    val isV3 = (json \ "openapi").isDefined
    val refPrefix = if (isV3) "#/components/schemas/" else "#/definitions/"

    val schemasObj: JsObject =
      if (isV3)
        (json \ "components" \ "schemas").asOpt[JsObject].getOrElse(Json.obj())
      else
        (json \ "definitions").asOpt[JsObject].getOrElse(Json.obj())

    // Strip package prefix, trailing [], and any configured class-name prefix.
    // If stripping [] creates a collision with an existing plain key, suffix with "List".
    val rawRenames: Seq[(String, String)] =
      schemasObj.keys.toSeq.map { k =>
        val base = k.split('.').last.stripSuffix("[]")
        k -> base
      }
    val shortNameUsed: Set[String] = rawRenames.map(_._2).groupBy(identity).filter(_._2.size > 1).keySet
    val renameMap: Map[String, String] =
      rawRenames.map { case (k, short) =>
        if (shortNameUsed(short) && k.endsWith("[]")) k -> (short + "List")
        else k -> short
      }.toMap

    def rewriteRefs(v: JsValue): JsValue = v match {
      case obj: JsObject =>
        JsObject(obj.fields.map {
          case ("$ref", JsString(ref)) if ref.startsWith(refPrefix) =>
            val name = ref.stripPrefix(refPrefix)
            "$ref" -> JsString(refPrefix + renameMap.getOrElse(name, name))
          case (k, vv) => k -> rewriteRefs(vv)
        })
      case arr: JsArray => JsArray(arr.value.map(rewriteRefs))
      case other => other
    }

    val rewritten = rewriteRefs(json).as[JsObject]

    val shortSchemas = JsObject(
      schemasObj.fields.map { case (k, v) => renameMap.getOrElse(k, k) -> rewriteRefs(v) }
    )

    if (isV3) {
      val components = (rewritten \ "components").asOpt[JsObject].getOrElse(Json.obj())
      rewritten ++ Json.obj("components" -> (components ++ Json.obj("schemas" -> shortSchemas)))
    } else {
      rewritten ++ Json.obj("definitions" -> shortSchemas)
    }
  }
}
