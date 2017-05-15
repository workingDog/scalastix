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
    val theList = for {h <- hash.kvList} yield h._1 -> Json.fromString(h._2)
    Json.obj(theList: _*)
  }

  implicit val decodeHashesType: Decoder[HashesType] = (c: HCursor) =>
    for {s <- c.value.as[List[Tuple2[String, String]]]} yield new HashesType(s)

}

// todo ---> a hack until I understand this better
case class Extensions(value: Map[String, Any])

object Extensions {

  implicit val encodeExtensions: Encoder[Extensions] = (ext: Extensions) => {
    val theSet = for {key <- ext.value.keySet} yield {
      ext.value(key) match {
        case s: String => key -> s.asJson
        case s: Double => key -> s.asJson
        case s: Int => key -> s.asJson
        case s: Boolean => key -> s.asJson
        case s: Array[String] => key -> s.asJson
        case s: Array[Double] => key -> s.asJson
        case s: Array[Int] => key -> s.asJson
        case s: Array[Boolean] => key -> s.asJson
        case s => key -> s.toString.asJson
      }
    }
    Json.obj(theSet.toList: _*)
  }

  implicit val decodeExtensions: Decoder[Extensions] = (c: HCursor) => {
    val valueOpt = c match {
      case x if x.as[Map[String, String]].isRight => x.as[Map[String, String]].toOption
      case x if x.as[Map[String, Double]].isRight => x.as[Map[String, Double]].toOption
      case x if x.as[Map[String, Int]].isRight => x.as[Map[String, Int]].toOption
      case x if x.as[Map[String, Boolean]].isRight => x.as[Map[String, Boolean]].toOption
      case x if x.as[Map[String, Array[String]]].isRight => x.as[Map[String, Array[String]]].toOption
      case x if x.as[Map[String, Array[Double]]].isRight => x.as[Map[String, Array[Double]]].toOption
      case x if x.as[Map[String, Array[Int]]].isRight => x.as[Map[String, Array[Int]]].toOption
      case x if x.as[Map[String, Array[Boolean]]].isRight => x.as[Map[String, Array[Boolean]]].toOption
      case _ => None
    }
    valueOpt match {
      case Some(x) => Right(new Extensions(x))
      case None => Left(DecodingFailure("Error in Extensions decoding", c.history))
    }
  }
}

/**
  * common properties of Observables
  */
sealed trait Observable {
  val `type`: String
  val description: Option[String]
  val extensions: Option[Extensions]
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
                            extensions: Option[Extensions] = None) extends Observable {

  def this(mime_type: Option[String], payload_bin: String, description: Option[String], extensions: Option[Extensions]) =
    this(Artifact.`type`, mime_type, Option(payload_bin), None, None, description, extensions)

  def this(mime_type: Option[String], url: String, hashes: HashesType, description: Option[String], extensions: Option[Extensions]) =
    this(Artifact.`type`, mime_type, None, Option(url), Option(hashes), description, extensions)

}

object Artifact {
  val `type` = "artifact"
}

/**
  * represents the properties of an Autonomous System (AS).
  */
case class AutonomousSystem(`type`: String = AutonomousSystem.`type`,
                            number: Int,
                            name: Option[String] = None,
                            rir: Option[String] = None,
                            description: Option[String] = None,
                            extensions: Option[Extensions] = None) extends Observable

object AutonomousSystem {
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
                     extensions: Option[Extensions] = None) extends Observable

object Directory {
  val `type` = "directory"
}

/**
  * The Domain Name represents the properties of a network domain name.
  */
case class DomainName(`type`: String = DomainName.`type`, value: String,
                      resolves_to_refs: Option[List[String]] = None, // todo object-ref must be ipv4-addr or ipv6-addr or domain-name
                      description: Option[String] = None,
                      extensions: Option[Extensions] = None) extends Observable

object DomainName {
  val `type` = "domain-name"
}

/**
  * The Email Address Object represents a single email address.
  */
case class EmailAddress(`type`: String = EmailAddress.`type`, value: String,
                        display_name: Option[String] = None,
                        belongs_to_ref: Option[String] = None, // todo  must be of type user-account
                        description: Option[String] = None,
                        extensions: Option[Extensions] = None) extends Observable

object EmailAddress {
  val `type` = "email-addr"
}

/**
  * Specifies one component of a multi-part email body.
  */
case class EmailMimeType(`type`: String = EmailMimeType.`type`,
                     body: Option[String] = None,
                     body_raw_ref: Option[String] = None, // todo MUST be of type artifact or file.
                     content_type: Option[String] = None,
                     content_disposition: Option[String] = None)

object EmailMimeType {
  val `type` = "mime-part-type"
}

/**
  * The Email Message Object represents an instance of an email message, corresponding to the internet message format
  * described in [RFC5322] and related RFCs.
  */
case class EmailMessage(`type`: String = EmailMessage.`type`,
                        is_multipart: Boolean,
                        body_multipart: Option[List[EmailMimeType]] = None, // todo mime-part-type and only if is_multipart=true
                        body: Option[String] = None, // todo only if is_multipart=false
                        date: Option[Timestamp] = None,
                        content_type: Option[String] = None,
                        from_ref: Option[String] = None, // todo must be of type email-address
                        sender_ref: Option[String] = None, // todo must be of type email-address
                        to_refs: Option[List[String]] = None, // todo must be of type email-address
                        cc_refs: Option[List[String]] = None, // todo must be of type email-address
                        bcc_refs: Option[List[String]] = None, // todo must be of type email-address
                        subject: Option[String] = None,
                        received_lines: Option[List[String]] = None,
                        additional_header_fields: Option[Map[String, String]] = None,
                        raw_email_ref: Option[String] = None, // todo must be of type artifact
                        description: Option[String] = None,
                        extensions: Option[Extensions] = None) extends Observable

object EmailMessage {
  val `type` = "email-message"
}

/**
  * The File Object represents the properties of a file.
  */
case class File(`type`: String = File.`type`,
                hashes: Option[HashesType] = None,
                size: Option[Int] = None,
                name: Option[String] = None,
                name_enc: Option[String] = None,
                magic_number_hex: Option[String] = None, // hex
                mime_type: Option[String] = None,
                created: Option[Timestamp] = None,
                modified: Option[Timestamp] = None,
                accessed: Option[Timestamp] = None,
                parent_directory_ref: Option[String] = None,
                is_encrypted: Option[Boolean] = None,
                encryption_algorithm: Option[String] = None,
                decryption_key: Option[String] = None,
                contains_refs: Option[List[String]] = None,
                content_ref: Option[String] = None,
                description: Option[String] = None,
                extensions: Option[Extensions] = None) extends Observable

object File {
  val `type` = "file"
}

/**
  * The IPv4 Address Object represents one or more IPv4 addresses expressed using CIDR notation.
  */
case class IPv4Address(`type`: String = IPv4Address.`type`,
                       value: String,
                       resolves_to_refs: Option[List[String]] = None,
                       belongs_to_refs: Option[List[String]] = None,
                       description: Option[String] = None,
                       extensions: Option[Extensions] = None) extends Observable

object IPv4Address {
  val `type` = "ipv4-addr"
}

/**
  * The IPv6 Address Object represents one or more IPv6 addresses expressed using CIDR notation.
  */
case class IPv6Address(`type`: String = IPv6Address.`type`,
                       value: String,
                       resolves_to_refs: Option[List[String]] = None,
                       belongs_to_refs: Option[List[String]] = None,
                       description: Option[String] = None,
                       extensions: Option[Extensions] = None) extends Observable

object IPv6Address {
  val `type` = "ipv6-addr"
}

/**
  * The MAC Address Object represents a single Media Access Control (MAC) address.
  */
case class MACAddress(`type`: String = MACAddress.`type`,
                      value: String,
                      description: Option[String] = None,
                      extensions: Option[Extensions] = None) extends Observable

object MACAddress {
  val `type` = "mac-addr"
}


/**
  * The Mutex Object represents the properties of a mutual exclusion (mutex) object.
  */
case class Mutex(`type`: String = Mutex.`type`,
                 name: String,
                 description: Option[String] = None,
                 extensions: Option[Extensions] = None) extends Observable

object Mutex {
  val `type` = "mutex"
}

/**
  * The Network Traffic Object represents arbitrary network traffic that originates from a source and is addressed to a destination.
  */
case class NetworkTraffic(`type`: String = NetworkTraffic.`type`,
                          start: Option[Timestamp] = None,
                          end: Option[Timestamp] = None,
                          is_active: Option[Boolean] = None,
                          src_ref: Option[String] = None,
                          dst_ref: Option[String] = None,
                          src_port: Option[Int] = None,
                          dst_port: Option[Int] = None,
                          protocols: Option[List[String]] = None,
                          src_byte_count: Option[Int] = None,
                          dst_byte_count: Option[Int] = None,
                          src_packets: Option[Int] = None,
                          dst_packets: Option[Int] = None,
                          ipfix: Option[Map[String, Either[Int, String]]] = None,
                          src_payload_ref: Option[String] = None,
                          dst_payload_ref: Option[String] = None,
                          encapsulates_refs: Option[List[String]] = None,
                          encapsulated_by_ref: Option[String] = None,
                          description: Option[String] = None,
                          extensions: Option[Extensions] = None) extends Observable

object NetworkTraffic {
  val `type` = "network-traffic"
}

/**
  * The Process Object represents common properties of an instance of a computer program as executed on an operating system.
  */
case class Process(`type`: String = Process.`type`,
                   is_hidden: Option[Boolean] = None,
                   pid: Option[Int] = None,
                   name: Option[String] = None,
                   created: Option[Timestamp] = None,
                   cwd: Option[String] = None,
                   arguments: Option[List[String]] = None,
                   command_line: Option[String] = None,
                   environment_variables: Option[Map[String, String]] = None,
                   opened_connection_refs: Option[List[String]] = None,
                   creator_user_ref: Option[String] = None,
                   binary_ref: Option[String] = None,
                   parent_ref: Option[String] = None,
                   child_refs: Option[List[String]] = None,
                   description: Option[String] = None,
                   extensions: Option[Extensions] = None) extends Observable

object Process {
  val `type` = "process"
}

/**
  * The Software Object represents high-level properties associated with software, including software products.
  */
case class Software(`type`: String = Software.`type`,
                    name: String,
                    cpe: Option[String] = None,
                    languages: Option[List[String]] = None,
                    vendor: Option[String] = None,
                    version: Option[String] = None,
                    description: Option[String] = None,
                    extensions: Option[Extensions] = None) extends Observable

object Software {
  val `type` = "software"
}

/**
  * The URL Object represents the properties of a uniform resource locator (URL).
  */
case class URL(`type`: String = URL.`type`,
               value: String,
               description: Option[String] = None,
               extensions: Option[Extensions] = None) extends Observable

object URL {
  val `type` = "url"
}

/**
  * The User Account Object represents an instance of any type of user account, including but not limited to operating system, device, messaging service, and social media platform accounts.
  */
case class UserAccount(`type`: String = UserAccount.`type`,
                       user_id: String,
                       account_login: Option[String] = None,
                       account_type: Option[String] = None,
                       display_name: Option[String] = None,
                       is_service_account: Option[Boolean] = None,
                       is_privileged: Option[Boolean] = None,
                       can_escalate_privs: Option[Boolean] = None,
                       is_disabled: Option[Boolean] = None,
                       account_created: Option[Timestamp] = None,
                       account_expires: Option[Timestamp] = None,
                       password_last_changed: Option[Timestamp] = None,
                       account_first_login: Option[Timestamp] = None,
                       account_last_login: Option[Timestamp] = None,
                       description: Option[String] = None,
                       extensions: Option[Extensions] = None) extends Observable

object UserAccount {
  val `type` = "user-account"
}

/**
  * The Windows Registry Value type captures the properties of a Windows Registry Key Value.
  */
case class WindowsRegistryValueType(`type`: String = WindowsRegistryValueType.`type`,
                                    name: String,
                                    data: Option[String] = None,
                                    data_type: Option[String] = None)

object WindowsRegistryValueType {
  val `type` = "windows-registry-value-type"
}

/**
  * The Registry Key Object represents the properties of a Windows registry key.
  */
case class WindowsRegistryKey(`type`: String = WindowsRegistryKey.`type`,
                              key: String,
                              values: Option[List[WindowsRegistryValueType]] = None,
                              modified: Option[Timestamp] = None,
                              creator_user_ref: Option[String] = None,
                              number_of_subkeys: Option[Int] = None,
                              description: Option[String] = None,
                              extensions: Option[Extensions] = None) extends Observable

object WindowsRegistryKey {
  val `type` = "windows-registry-key"
}

/**
  * The X.509 v3 Extensions type captures properties associated with X.509 v3 extensions, which serve as a mechanism for specifying additional information such as alternative subject names.
  */
case class X509V3ExtenstionsType(`type`: String = X509V3ExtenstionsType.`type`,
                                 basic_constraints: Option[String] = None,
                                 name_constraints: Option[String] = None,
                                 policy_constraints: Option[String] = None,
                                 key_usage: Option[String] = None,
                                 extended_key_usage: Option[String] = None,
                                 subject_key_identifier: Option[String] = None,
                                 authority_key_identifier: Option[String] = None,
                                 subject_alternative_name: Option[String] = None,
                                 issuer_alternative_name: Option[String] = None,
                                 subject_directory_attributes: Option[String] = None,
                                 crl_distribution_points: Option[String] = None,
                                 inhibit_any_policy: Option[Int] = None,
                                 private_key_usage_period_not_before: Option[Timestamp] = None,
                                 private_key_usage_period_not_after: Option[Timestamp] = None)

object X509V3ExtenstionsType {
  val `type` = "x509-v3-extensions-type"
}

/**
  * The X.509 Certificate Object represents the properties of an X.509 certificate, as defined by ITU recommendation X.509 [X.509].
  */
case class X509Certificate(`type`: String = X509Certificate.`type`,
                           is_self_signed: Option[Boolean] = None,
                           hashes: Option[HashesType] = None,
                           version: Option[String] = None,
                           serial_number: Option[String] = None,
                           signature_algorithm: Option[String] = None,
                           issuer: Option[String] = None,
                           validity_not_before: Option[Timestamp] = None,
                           validity_not_after: Option[Timestamp] = None,
                           subject: Option[String] = None,
                           subject_public_key_algorithm: Option[String] = None,
                           subject_public_key_modulus: Option[String] = None,
                           subject_public_key_exponent: Option[Int] = None,
                           x509_v3_extensions: Option[X509V3ExtenstionsType] = None,
                           description: Option[String] = None,
                           extensions: Option[Extensions] = None) extends Observable

object X509Certificate {
  val `type` = "x509-certificate"
}





