package com.kodekutters.stix

import play.api.libs.json._
import Util._
import play.api.libs.functional.syntax.unlift
import play.api.libs.functional.syntax._

/**
  * STIX-2.1 protocol, Cyber Observable Objects
  *
  * STIX Cyber Observables document the facts concerning what happened on a network or host,
  * but not necessarily the who or when, and never the why.
  *
  * reference: https://oasis-open.github.io/cti-documentation/
  *
  * Author: R. Wathelet May 2017
  *
  */

/**
  * common properties of Observables
  * To create a Custom Observable, simply extends this trait
  */
trait Observable {
  val `type`: String
  val extensions: Option[Map[String, Extension]]
  val custom: Option[CustomProps]
}

/**
  * The Artifact Object permits capturing an array of bytes (8-bits), as a base64-encoded string,
  * or linking to a file-like payload.
  */
case class Artifact(`type`: String = Artifact.`type`,
                    mime_type: Option[String] = None,
                    payload_bin: Option[String] = None, // base64-encoded string
                    url: Option[String] = None,
                    hashes: Option[Map[String, String]] = None,
                    extensions: Option[Map[String, Extension]] = None,
                    custom: Option[CustomProps] = None) extends Observable {

  def this(mime_type: Option[String], payload_bin: String,
           extensions: Option[Map[String, Extension]], custom: Option[CustomProps]) =
    this(Artifact.`type`, mime_type, Option(payload_bin), None, None, extensions, custom)

  def this(mime_type: Option[String], url: String, hashes: Map[String, String],
           extensions: Option[Map[String, Extension]], custom: Option[CustomProps]) =
    this(Artifact.`type`, mime_type, None, Option(url), Option(hashes), extensions, custom)

}

object Artifact {
  val `type` = "artifact"

  implicit val fmt: Format[Artifact] = (
    (__ \ "type").format[String] and
      (__ \ "mime_type").formatNullable[String] and
      (__ \ "payload_bin").formatNullable[String] and
      (__ \ "url").formatNullable[String] and
      (__ \ "hashes").formatNullable[Map[String, String]] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (Artifact.apply, unlift(Artifact.unapply))

}

/**
  * represents the properties of an Autonomous System (AS).
  */
case class AutonomousSystem(`type`: String = AutonomousSystem.`type`,
                            number: Int,
                            name: Option[String] = None,
                            rir: Option[String] = None,
                            extensions: Option[Map[String, Extension]] = None,
                            custom: Option[CustomProps] = None) extends Observable

object AutonomousSystem {
  val `type` = "autonomous-system"

  implicit val fmt: Format[AutonomousSystem] = (
    (__ \ "type").format[String] and
      (__ \ "number").format[Int] and
      (__ \ "name").formatNullable[String] and
      (__ \ "rir").formatNullable[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (AutonomousSystem.apply, unlift(AutonomousSystem.unapply))

}

/**
  * The Directory Object represents the properties common to a file system directory.
  */
case class Directory(`type`: String = Directory.`type`,
                     path: String,
                     path_enc: Option[String] = None,
                     created: Option[Timestamp] = None,
                     modified: Option[Timestamp] = None,
                     accessed: Option[Timestamp] = None,
                     contains_refs: Option[List[String]] = None, // todo object-ref must be file or directory type
                     extensions: Option[Map[String, Extension]] = None,
                     custom: Option[CustomProps] = None) extends Observable

object Directory {
  val `type` = "directory"

  implicit val fmt: Format[Directory] = (
    (__ \ "type").format[String] and
      (__ \ "path").format[String] and
      (__ \ "path_enc").formatNullable[String] and
      (__ \ "created").formatNullable[Timestamp] and
      (__ \ "modified").formatNullable[Timestamp] and
      (__ \ "accessed").formatNullable[Timestamp] and
      (__ \ "contains_refs").formatNullable[List[String]] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (Directory.apply, unlift(Directory.unapply))

}

/**
  * The Domain Name represents the properties of a network domain name.
  */
case class DomainName(`type`: String = DomainName.`type`,
                      value: String,
                      resolves_to_refs: Option[List[String]] = None, // todo object-ref must be ipv4-addr or ipv6-addr or domain-name
                      extensions: Option[Map[String, Extension]] = None,
                      custom: Option[CustomProps] = None) extends Observable

object DomainName {
  val `type` = "domain-name"

  implicit val fmt: Format[DomainName] = (
    (__ \ "type").format[String] and
      (__ \ "value").format[String] and
      (__ \ "resolves_to_refs").formatNullable[List[String]] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (DomainName.apply, unlift(DomainName.unapply))

}

/**
  * The Email Address Object represents a single email address.
  */
case class EmailAddress(`type`: String = EmailAddress.`type`, value: String,
                        display_name: Option[String] = None,
                        belongs_to_ref: Option[String] = None, // todo  must be of type user-account
                        extensions: Option[Map[String, Extension]] = None,
                        custom: Option[CustomProps] = None) extends Observable

object EmailAddress {
  val `type` = "email-addr"

  implicit val fmt: Format[EmailAddress] = (
    (__ \ "type").format[String] and
      (__ \ "value").format[String] and
      (__ \ "display_name").formatNullable[String] and
      (__ \ "belongs_to_ref").formatNullable[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (EmailAddress.apply, unlift(EmailAddress.unapply))

}

/**
  * Specifies one component of a multi-part email body.
  */
case class EmailMimeType(`type`: String = EmailMimeType.`type`,
                         body: Option[String] = None,
                         body_raw_ref: Option[String] = None, // todo must be of type artifact or file.
                         content_type: Option[String] = None,
                         content_disposition: Option[String] = None,
                         custom: Option[CustomProps] = None)

object EmailMimeType {
  val `type` = "mime-part-type"

  implicit val fmt: Format[EmailMimeType] = (
    (__ \ "type").format[String] and
      (__ \ "body").formatNullable[String] and
      (__ \ "content_type").formatNullable[String] and
      (__ \ "content_type").formatNullable[String] and
      (__ \ "content_disposition").formatNullable[String] and
      JsPath.formatNullable[CustomProps]
    ) (EmailMimeType.apply, unlift(EmailMimeType.unapply))

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
                        extensions: Option[Map[String, Extension]] = None,
                        custom: Option[CustomProps] = None) extends Observable

object EmailMessage {
  val `type` = "email-message"

  implicit val fmt: Format[EmailMessage] = (
    (__ \ "type").format[String] and
      (__ \ "is_multipart").format[Boolean] and
      (__ \ "body_multipart").formatNullable[List[EmailMimeType]] and
      (__ \ "body").formatNullable[String] and
      (__ \ "date").formatNullable[Timestamp] and
      (__ \ "content_type").formatNullable[String] and
      (__ \ "from_ref").formatNullable[String] and
      (__ \ "sender_ref").formatNullable[String] and
      (__ \ "to_refs").formatNullable[List[String]] and
      (__ \ "cc_refs").formatNullable[List[String]] and
      (__ \ "bcc_refs").formatNullable[List[String]] and
      (__ \ "subject").formatNullable[String] and
      (__ \ "received_lines").formatNullable[List[String]] and
      (__ \ "additional_header_fields").formatNullable[Map[String, String]] and
      (__ \ "raw_email_ref").formatNullable[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (EmailMessage.apply, unlift(EmailMessage.unapply))

}

/**
  * The File Object represents the properties of a file.
  */
case class File(`type`: String = File.`type`,
                hashes: Option[Map[String, String]] = None,
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
                extensions: Option[Map[String, Extension]] = None,
                custom: Option[CustomProps] = None) extends Observable

object File {
  val `type` = "file"

  implicit val fmt: Format[File] = (
    (__ \ "type").format[String] and
      (__ \ "hashes").formatNullable[Map[String, String]] and
      (__ \ "size").formatNullable[Int] and
      (__ \ "name").formatNullable[String] and
      (__ \ "name_enc").formatNullable[String] and
      (__ \ "magic_number_hex").formatNullable[String] and
      (__ \ "mime_type").formatNullable[String] and
      (__ \ "created").formatNullable[Timestamp] and
      (__ \ "modified").formatNullable[Timestamp] and
      (__ \ "accessed").formatNullable[Timestamp] and
      (__ \ "parent_directory_ref").formatNullable[String] and
      (__ \ "is_encrypted").formatNullable[Boolean] and
      (__ \ "encryption_algorithm").formatNullable[String] and
      (__ \ "decryption_key").formatNullable[String] and
      (__ \ "contains_refs").formatNullable[List[String]] and
      (__ \ "content_ref").formatNullable[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (File.apply, unlift(File.unapply))

}

/**
  * The IPv4 Address Object represents one or more IPv4 addresses expressed using CIDR notation.
  */
case class IPv4Address(`type`: String = IPv4Address.`type`,
                       value: String,
                       resolves_to_refs: Option[List[String]] = None,
                       belongs_to_refs: Option[List[String]] = None,
                       extensions: Option[Map[String, Extension]] = None,
                       custom: Option[CustomProps] = None) extends Observable

object IPv4Address {
  val `type` = "ipv4-addr"

  implicit val fmt: Format[IPv4Address] = (
    (__ \ "type").format[String] and
      (__ \ "value").format[String] and
      (__ \ "resolves_to_refs").formatNullable[List[String]] and
      (__ \ "belongs_to_refs").formatNullable[List[String]] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (IPv4Address.apply, unlift(IPv4Address.unapply))

}

/**
  * The IPv6 Address Object represents one or more IPv6 addresses expressed using CIDR notation.
  */
case class IPv6Address(`type`: String = IPv6Address.`type`,
                       value: String,
                       resolves_to_refs: Option[List[String]] = None,
                       belongs_to_refs: Option[List[String]] = None,
                       extensions: Option[Map[String, Extension]] = None,
                       custom: Option[CustomProps] = None) extends Observable

object IPv6Address {
  val `type` = "ipv6-addr"

  implicit val fmt: Format[IPv6Address] = (
    (__ \ "type").format[String] and
      (__ \ "value").format[String] and
      (__ \ "resolves_to_refs").formatNullable[List[String]] and
      (__ \ "belongs_to_refs").formatNullable[List[String]] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (IPv6Address.apply, unlift(IPv6Address.unapply))

}

/**
  * The MAC Address Object represents a single Media Access Control (MAC) address.
  */
case class MACAddress(`type`: String = MACAddress.`type`,
                      value: String,
                      extensions: Option[Map[String, Extension]] = None,
                      custom: Option[CustomProps] = None) extends Observable

object MACAddress {
  val `type` = "mac-addr"

  implicit val fmt: Format[MACAddress] = (
    (__ \ "type").format[String] and
      (__ \ "value").format[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (MACAddress.apply, unlift(MACAddress.unapply))

}

/**
  * The Mutex Object represents the properties of a mutual exclusion (mutex) object.
  */
case class Mutex(`type`: String = Mutex.`type`,
                 name: String,
                 extensions: Option[Map[String, Extension]] = None,
                 custom: Option[CustomProps] = None) extends Observable

object Mutex {
  val `type` = "mutex"

  implicit val fmt: Format[Mutex] = (
    (__ \ "type").format[String] and
      (__ \ "name").format[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (Mutex.apply, unlift(Mutex.unapply))

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
                          extensions: Option[Map[String, Extension]] = None,
                          custom: Option[CustomProps] = None) extends Observable

object NetworkTraffic {
  val `type` = "network-traffic"

  implicit val fmt: Format[NetworkTraffic] = (
    (__ \ "type").format[String] and
      (__ \ "start").formatNullable[Timestamp] and
      (__ \ "end").formatNullable[Timestamp] and
      (__ \ "is_active").formatNullable[Boolean] and
      (__ \ "src_ref").formatNullable[String] and
      (__ \ "dst_ref").formatNullable[String] and
      (__ \ "src_port").formatNullable[Int] and
      (__ \ "dst_port").formatNullable[Int] and
      (__ \ "protocols").formatNullable[List[String]] and
      (__ \ "src_byte_count").formatNullable[Int] and
      (__ \ "dst_byte_count").formatNullable[Int] and
      (__ \ "src_packets").formatNullable[Int] and
      (__ \ "dst_packets").formatNullable[Int] and
      (__ \ "ipfix").formatNullable[Map[String, Either[Int, String]]] and
      (__ \ "src_payload_ref").formatNullable[String] and
      (__ \ "dst_payload_ref").formatNullable[String] and
      (__ \ "encapsulates_refs").formatNullable[List[String]] and
      (__ \ "encapsulated_by_ref").formatNullable[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (NetworkTraffic.apply, unlift(NetworkTraffic.unapply))

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
                   extensions: Option[Map[String, Extension]] = None,
                   custom: Option[CustomProps] = None) extends Observable

object Process {
  val `type` = "process"

  implicit val fmt: Format[Process] = (
    (__ \ "type").format[String] and
      (__ \ "is_hidden").formatNullable[Boolean] and
      (__ \ "pid").formatNullable[Int] and
      (__ \ "name").formatNullable[String] and
      (__ \ "created").formatNullable[Timestamp] and
      (__ \ "cwd").formatNullable[String] and
      (__ \ "arguments").formatNullable[List[String]] and
      (__ \ "command_line").formatNullable[String] and
      (__ \ "environment_variables").formatNullable[Map[String, String]] and
      (__ \ "opened_connection_refs").formatNullable[List[String]] and
      (__ \ "creator_user_ref").formatNullable[String] and
      (__ \ "binary_ref").formatNullable[String] and
      (__ \ "parent_ref").formatNullable[String] and
      (__ \ "child_refs").formatNullable[List[String]] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (Process.apply, unlift(Process.unapply))

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
                    extensions: Option[Map[String, Extension]] = None,
                    custom: Option[CustomProps] = None) extends Observable

object Software {
  val `type` = "software"

  implicit val fmt: Format[Software] = (
    (__ \ "type").format[String] and
      (__ \ "name").format[String] and
      (__ \ "cpe").formatNullable[String] and
      (__ \ "languages").formatNullable[List[String]] and
      (__ \ "vendor").formatNullable[String] and
      (__ \ "version").formatNullable[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (Software.apply, unlift(Software.unapply))

}

/**
  * The URL Object represents the properties of a uniform resource locator (URL).
  */
case class URL(`type`: String = URL.`type`,
               value: String,
               extensions: Option[Map[String, Extension]] = None,
               custom: Option[CustomProps] = None) extends Observable

object URL {
  val `type` = "url"

  implicit val fmt: Format[URL] = (
    (__ \ "type").format[String] and
      (__ \ "value").format[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (URL.apply, unlift(URL.unapply))

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
                       extensions: Option[Map[String, Extension]] = None,
                       custom: Option[CustomProps] = None) extends Observable

object UserAccount {
  val `type` = "user-account"

  implicit val fmt: Format[UserAccount] = (
    (__ \ "type").format[String] and
      (__ \ "user_id").format[String] and
      (__ \ "account_login").formatNullable[String] and
      (__ \ "account_type").formatNullable[String] and
      (__ \ "display_name").formatNullable[String] and
      (__ \ "is_service_account").formatNullable[Boolean] and
      (__ \ "is_privileged").formatNullable[Boolean] and
      (__ \ "can_escalate_privs").formatNullable[Boolean] and
      (__ \ "is_disabled").formatNullable[Boolean] and
      (__ \ "account_created").formatNullable[Timestamp] and
      (__ \ "account_expires").formatNullable[Timestamp] and
      (__ \ "password_last_changed").formatNullable[Timestamp] and
      (__ \ "account_first_login").formatNullable[Timestamp] and
      (__ \ "account_last_login").formatNullable[Timestamp] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (UserAccount.apply, unlift(UserAccount.unapply))

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

  implicit val fmt = Json.format[WindowsRegistryValueType]
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
                              extensions: Option[Map[String, Extension]] = None,
                              custom: Option[CustomProps] = None) extends Observable

object WindowsRegistryKey {
  val `type` = "windows-registry-key"

  implicit val fmt: Format[WindowsRegistryKey] = (
    (__ \ "type").format[String] and
      (__ \ "key").format[String] and
      (__ \ "values").formatNullable[List[WindowsRegistryValueType]] and
      (__ \ "modified").formatNullable[Timestamp] and
      (__ \ "creator_user_ref").formatNullable[String] and
      (__ \ "number_of_subkeys").formatNullable[Int] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (WindowsRegistryKey.apply, unlift(WindowsRegistryKey.unapply))

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
  implicit val fmt = Json.format[X509V3ExtenstionsType]
}

/**
  * The X.509 Certificate Object represents the properties of an X.509 certificate, as defined by ITU recommendation X.509 [X.509].
  */
case class X509Certificate(`type`: String = X509Certificate.`type`,
                           is_self_signed: Option[Boolean] = None,
                           hashes: Option[Map[String, String]] = None,
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
                           extensions: Option[Map[String, Extension]] = None,
                           custom: Option[CustomProps] = None) extends Observable

object X509Certificate {
  val `type` = "x509-certificate"

  implicit val fmt: Format[X509Certificate] = (
    (__ \ "type").format[String] and
      (__ \ "is_self_signed").formatNullable[Boolean] and
      (__ \ "hashes").formatNullable[Map[String, String]] and
      (__ \ "version").formatNullable[String] and
      (__ \ "serial_number").formatNullable[String] and
      (__ \ "signature_algorithm").formatNullable[String] and
      (__ \ "issuer").formatNullable[String] and
      (__ \ "validity_not_before").formatNullable[Timestamp] and
      (__ \ "validity_not_after").formatNullable[Timestamp] and
      (__ \ "subject").formatNullable[String] and
      (__ \ "subject_public_key_algorithm").formatNullable[String] and
      (__ \ "subject_public_key_modulus").formatNullable[String] and
      (__ \ "subject_public_key_exponent").formatNullable[Int] and
      (__ \ "x509_v3_extensions").formatNullable[X509V3ExtenstionsType] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (X509Certificate.apply, unlift(X509Certificate.unapply))

}

/**
  * A custom observable object
  */
case class CustomObservable(`type`: String = CustomObservable.`type`,
                            extensions: Option[Map[String, Extension]] = None,
                            custom: Option[CustomProps] = None) extends Observable

object CustomObservable {
  val `type` = "x-custom-observable"

  implicit val fmt: Format[CustomObservable] = (
    (__ \ "type").format[String] and
      (__ \ "extensions").formatNullable[Map[String, Extension]] and
      JsPath.formatNullable[CustomProps]
    ) (CustomObservable.apply, unlift(CustomObservable.unapply))
}

/**
  * STIX Cyber Observables document
  */
object Observable {

  val theReads = new Reads[Observable] {
    def reads(js: JsValue): JsResult[Observable] = {
      (js \ "type").asOpt[String].map({
        case Artifact.`type` => Artifact.fmt.reads(js)
        case AutonomousSystem.`type` => AutonomousSystem.fmt.reads(js)
        case Directory.`type` => Directory.fmt.reads(js)
        case DomainName.`type` => DomainName.fmt.reads(js)
        case EmailAddress.`type` => EmailAddress.fmt.reads(js)
        case EmailMessage.`type` => EmailMessage.fmt.reads(js)
        case File.`type` => File.fmt.reads(js)
        case IPv4Address.`type` => IPv4Address.fmt.reads(js)
        case IPv6Address.`type` => IPv6Address.fmt.reads(js)
        case MACAddress.`type` => MACAddress.fmt.reads(js)
        case Mutex.`type` => Mutex.fmt.reads(js)
        case NetworkTraffic.`type` => NetworkTraffic.fmt.reads(js)
        case Process.`type` => Process.fmt.reads(js)
        case Software.`type` => Software.fmt.reads(js)
        case URL.`type` => URL.fmt.reads(js)
        case UserAccount.`type` => UserAccount.fmt.reads(js)
        case WindowsRegistryKey.`type` => WindowsRegistryKey.fmt.reads(js)
        case X509Certificate.`type` => X509Certificate.fmt.reads(js)
        case _ => CustomObservable.fmt.reads(js)
        // todo ---> custom observables
      }).getOrElse(JsError("Error reading Observable"))
    }
  }

  val theWrites = new Writes[Observable] {
    def writes(obj: Observable): JsValue = {
      obj match {
        case ext: Artifact => Artifact.fmt.writes(ext)
        case ext: AutonomousSystem => AutonomousSystem.fmt.writes(ext)
        case ext: Directory => Directory.fmt.writes(ext)
        case ext: DomainName => DomainName.fmt.writes(ext)
        case ext: EmailAddress => EmailAddress.fmt.writes(ext)
        case ext: EmailMessage => EmailMessage.fmt.writes(ext)
        case ext: File => File.fmt.writes(ext)
        case ext: IPv4Address => IPv4Address.fmt.writes(ext)
        case ext: IPv6Address => IPv6Address.fmt.writes(ext)
        case ext: MACAddress => MACAddress.fmt.writes(ext)
        case ext: Mutex => Mutex.fmt.writes(ext)
        case ext: NetworkTraffic => NetworkTraffic.fmt.writes(ext)
        case ext: Process => Process.fmt.writes(ext)
        case ext: Software => Software.fmt.writes(ext)
        case ext: URL => URL.fmt.writes(ext)
        case ext: UserAccount => UserAccount.fmt.writes(ext)
        case ext: WindowsRegistryKey => WindowsRegistryKey.fmt.writes(ext)
        case ext: X509Certificate => X509Certificate.fmt.writes(ext)
        case ext: CustomObservable => CustomObservable.fmt.writes(ext)
        // todo ---> custom observables
        case _ => JsNull
      }
    }
  }

  implicit val fmt: Format[Observable] = Format(theReads, theWrites)
}
