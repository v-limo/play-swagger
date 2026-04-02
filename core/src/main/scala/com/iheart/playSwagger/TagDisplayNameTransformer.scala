package com.iheart.playSwagger

import scala.util.Try

import play.api.libs.json.*

class TagDisplayNameTransformer extends OutputTransformer {

  private val upperCaseTokens: Set[String] = Set("pim", "api", "id", "url", "http", "ui")

  private def toDisplayName(name: String): String =
    name.split("[-_]").map { str =>
      if (upperCaseTokens.contains(str.toLowerCase)) str.toUpperCase
      else str.capitalize
    }.mkString(" ")

  override def apply(json: JsObject): Try[JsObject] = Try {
    val paths = (json \ "paths").asOpt[JsObject].getOrElse(Json.obj())

    // collect all tag names from operations
    val operationTags = paths.values.flatMap {
      case item: JsObject => item.values.flatMap {
          case op: JsObject => (op \ "tags").asOpt[Seq[String]].getOrElse(Seq.empty)
          case _ => Seq.empty
        }
      case _ => Seq.empty
    }.toSeq.distinct.sorted

    val existingTags = (json \ "tags").asOpt[JsArray].getOrElse(Json.arr())

    val updated = JsArray(operationTags.map { name =>
      existingTags.value
        .find(t => (t \ "name").asOpt[String].contains(name)) match {
        case Some(existing: JsObject) if (existing \ "x-displayName").isDefined => existing
        case Some(existing: JsObject) => existing + ("x-displayName" -> JsString(toDisplayName(name)))
        case _ => Json.obj("name" -> name, "x-displayName" -> JsString(toDisplayName(name)))
      }
    })

    json + ("tags" -> updated)
  }
}
