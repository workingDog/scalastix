package com.kodekutters.stix

import java.time.{ZoneId, ZonedDateTime}
import java.util.UUID

import io.circe.syntax._
import io.circe.{Json, _}
import io.circe.generic.auto._
import io.circe.Decoder._
import io.circe._


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

// todo  <-----------

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
// todo hashes-type
case class Artifact private(`type`: String = Artifact.`type`,
                            mime_type: Option[String] = None,
                            payload_bin: Option[String] = None, // base64-encoded string
                            url: Option[String] = None,
                            hashes: Option[String] = None,
                            description: Option[String] = None,
                            extensions: Option[Map[String, String]] = None) extends Observable {

  def this(mime_type: Option[String], payload_bin: String, description: Option[String], extensions: Option[Map[String, String]]) =
    this(Artifact.`type`, mime_type, Option(payload_bin), None, None, description, extensions)

  def this(mime_type: Option[String], url: String, hashes: String, description: Option[String], extensions: Option[Map[String, String]]) =
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





