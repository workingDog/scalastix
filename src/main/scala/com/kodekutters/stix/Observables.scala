package com.kodekutters.stix

import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

import io.circe.syntax._
import io.circe.{Json, _}
import io.circe.generic.auto._
import io.circe.Decoder._
import io.circe._

import io.circe.parser._
import io.circe.parser.decode

import scala.language.implicitConversions
import scala.collection.mutable


/**
  * STIX-2.1 protocol, Cyber Observable Objects
  *
  * STIX Cyber Observables document the facts concerning what happened on a network or host,
  * but not necessarily the who or when, and never the why.
  *
  * https://docs.google.com/document/d/1b7ZahfoxIepkv3MoaxGvA4WUq6GaCl5S_VPKpIEEAm4/edit
  *
  * Author: R. Wathelet May 2017
  *
  */

// -----------> todo  <-----------

/**
  * The Hashes type represents 1 or more cryptographic hashes, as a special set of key/value pairs.
  *
  * @param kvList a list of (k,v) tuples
  */
case class HashesType(kvList: List[Tuple2[String, String]]) {

  def this(kv: Tuple2[String, String]) = this(List(kv))

  def this(kvArgs: Tuple2[String, String]*) = this(kvArgs.toList)

}

object HashesType {

  implicit val encodeHashesType: Encoder[HashesType] = (hash: HashesType) => {
    val theList = for {h <- hash.kvList} yield {
      h._1 -> Json.fromString(h._2)
    }
    Json.obj(theList: _*)
  }

  implicit val decodeHashesType: Decoder[HashesType] = (c: HCursor) =>
    for {s <- c.value.as[List[Tuple2[String, String]]]} yield new HashesType(s)

}

/**
  * common properties of Observables
  */
sealed trait Observable {
  val `type`: String
  val description: Option[String]
  val extensions: Option[Map[String, String]] // todo Map[String, Any]
}

/**
  * The Artifact Object permits capturing an array of bytes (8-bits), as a base64-encoded string,
  * or linking to a file-like payload.
  */
case class Artifact private(`type`: String = Artifact.`type`,
                            mime_type: Option[String] = None,
                            payload_bin: Option[String] = None, // base64-encoded string
                            url: Option[String] = None,
                            hashes: Option[HashesType] = None,
                            description: Option[String] = None,
                            extensions: Option[Map[String, String]] = None) extends Observable {

  def this(mime_type: Option[String], payload_bin: String, description: Option[String], extensions: Option[Map[String, String]]) =
    this(Artifact.`type`, mime_type, Option(payload_bin), None, None, description, extensions)

  def this(mime_type: Option[String], url: String, hashes: HashesType, description: Option[String], extensions: Option[Map[String, String]]) =
    this(Artifact.`type`, mime_type, None, Option(url), Option(hashes), description, extensions)

}

object Artifact {
  val `type` = "artifact"
}

/**
  * The AS object represents the properties of an Autonomous System (AS).
  */
case class ASObject(`type`: String = ASObject.`type`,
                    number: Int,
                    name: Option[String] = None,
                    rir: Option[String] = None,
                    description: Option[String] = None,
                    extensions: Option[Map[String, String]] = None) extends Observable

object ASObject {
  val `type` = "autonomous-system"
}

/**
  * The Directory Object represents the properties common to a file system directory.
  */
case class Directory(`type`: String = Directory.`type`, path: String,
                     path_enc: Option[String] = None,
                     created: Option[Timestamp] = None,
                     modified: Option[Timestamp] = None,
                     accessed: Option[Timestamp] = None,
                     contains_refs: Option[List[String]] = None, // todo object-ref must be file or directory type
                     description: Option[String] = None,
                     extensions: Option[Map[String, String]] = None) extends Observable

object Directory {
  val `type` = "directory"
}



