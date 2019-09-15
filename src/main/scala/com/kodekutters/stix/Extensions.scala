package com.kodekutters.stix

import play.api.libs.json._
import play.api.libs.functional.syntax._
import Util._


/**
  * STIX-2.0 Extensions
  *
  * reference: https://oasis-open.github.io/cti-documentation/
  *
  * Author: R. Wathelet 2017
  */


/**
  * represents a Predefined Cyber Observable Object Extension.
  * To create a Custom Object Extension, simply extends this trait
  */
trait Extension

/**
  * a generic unknown custom extension object
  */
case class CustomExtension(custom: Option[CustomProps] = None) extends Extension

object CustomExtension {
  val `type` = "custom_ext"
  implicit val fmt = Json.format[CustomExtension]
}

/**
  * The Archive File extension specifies a default extension for capturing properties specific to archive files.
  */
case class ArchiveFileExt(contains_refs: Option[List[String]] = None,
                          version: Option[String] = None,
                          comment: Option[String] = None) extends Extension

object ArchiveFileExt {
  val `type` = "archive-ext"
  implicit val fmt = Json.format[ArchiveFileExt]
}

/**
  * The Alternate Data Stream type represents an NTFS alternate data stream.
  */
case class AlternateDataStream(name: String,
                               hashes: Option[Map[String, String]] = None,
                               size: Option[Long] = None)

object AlternateDataStream {
  implicit val fmt = Json.format[AlternateDataStream]
}

/**
  * The NTFS file extension specifies a default extension for capturing properties specific to the storage of the file on the NTFS file system.
  */
case class NTFSFileExt(sid: Option[String] = None,
                       alternate_data_streams: Option[List[AlternateDataStream]] = None) extends Extension

object NTFSFileExt {
  val `type` = "ntfs-ext"
  implicit val fmt = Json.format[NTFSFileExt]
}

/**
  * The PDF file extension specifies a default extension for capturing properties specific to PDF files.
  */
case class PdfFileExt(version: Option[String] = None,
                      is_optimized: Option[Boolean] = None,
                      document_info_dict: Option[Map[String, String]] = None,
                      pdfid0: Option[String] = None,
                      pdfid1: Option[String] = None) extends Extension

object PdfFileExt {
  val `type` = "pdf-ext"
  implicit val fmt = Json.format[PdfFileExt]
}

/**
  * The Raster Image file extension specifies a default extension for capturing properties specific to raster image files.
  */
case class RasterImgExt(image_height: Option[Long] = None,
                        image_width: Option[Long] = None,
                        bits_per_pixel: Option[Long] = None,
                        image_compression_algorithm: Option[String] = None,
                        exif_tags: Option[Map[String, Either[Long, String]]] = None) extends Extension

object RasterImgExt {
  val `type` = "raster-image-ext"
  implicit val fmt = Json.format[RasterImgExt]
}

/**
  * The Windows PE Optional Header type represents the properties of the PE optional header.
  */
case class WindowPEOptionalHeaderType(magic_hex: Option[String] = None,
                                      major_linker_version: Option[Long] = None,
                                      minor_linker_version: Option[Long] = None,
                                      size_of_code: Option[Long] = None,
                                      size_of_initialized_data: Option[Long] = None,
                                      size_of_uninitialized_data: Option[Long] = None,
                                      address_of_entry_point: Option[Long] = None,
                                      base_of_code: Option[Long] = None,
                                      base_of_data: Option[Long] = None,
                                      image_base: Option[Long] = None,
                                      section_alignment: Option[Long] = None,
                                      file_alignment: Option[Long] = None,
                                      major_os_version: Option[Long] = None,
                                      minor_os_version: Option[Long] = None,
                                      major_image_version: Option[Long] = None,
                                      minor_image_version: Option[Long] = None,
                                      major_subsystem_version: Option[Long] = None,
                                      minor_subsystem_version: Option[Long] = None,
                                      win32_version_value_hex: Option[String] = None,
                                      size_of_image: Option[Long] = None,
                                      size_of_headers: Option[Long] = None,
                                      checksum_hex: Option[String] = None,
                                      dll_characteristics_hex: Option[String] = None,
                                      size_of_stack_reserve: Option[Long] = None,
                                      size_of_stack_commit: Option[Long] = None,
                                      size_of_heap_reserve: Option[Long] = None,
                                      size_of_heap_commit: Option[Long] = None,
                                      loader_flags_hex: Option[String] = None,
                                      number_of_rva_and_sizes: Option[Long] = None,
                                      hashes: Option[Map[String, String]] = None)

object WindowPEOptionalHeaderType {

  implicit object fmt extends Format[WindowPEOptionalHeaderType] {

    private val tagField1 = "magic_hex"
    private val tagField2 = "major_linker_version"
    private val tagField3 = "minor_linker_version"
    private val tagField4 = "size_of_code"
    private val tagField5 = "size_of_initialized_data"
    private val tagField6 = "size_of_uninitialized_data"
    private val tagField7 = "address_of_entry_point"
    private val tagField8 = "base_of_code"
    private val tagField9 = "base_of_data"
    private val tagField10 = "image_base"
    private val tagField11 = "section_alignment"
    private val tagField12 = "file_alignment"
    private val tagField13 = "major_os_version"
    private val tagField14 = "minor_os_version"
    private val tagField15 = "major_image_version"
    private val tagField16 = "minor_image_version"
    private val tagField17 = "major_subsystem_version"
    private val tagField18 = "minor_subsystem_version"
    private val tagField19 = "win32_version_value_hex"
    private val tagField20 = "size_of_image"
    private val tagField21 = "size_of_headers"
    private val tagField22 = "checksum_hex"
    private val tagField23 = "dll_characteristics_hex"
    private val tagField24 = "size_of_stack_reserve"
    private val tagField25 = "size_of_stack_commit"
    private val tagField26 = "size_of_heap_reserve"
    private val tagField27 = "size_of_heap_commit"
    private val tagField28 = "loader_flags_hex"
    private val tagField29 = "number_of_rva_and_sizes"
    private val tagField30 = "hashes"

    override def reads(json: JsValue): JsResult[WindowPEOptionalHeaderType] = JsSuccess(
      WindowPEOptionalHeaderType(
        (json \ tagField1).asOpt[String],
        (json \ tagField2).asOpt[Long],
        (json \ tagField3).asOpt[Long],
        (json \ tagField4).asOpt[Long],
        (json \ tagField5).asOpt[Long],
        (json \ tagField6).asOpt[Long],
        (json \ tagField7).asOpt[Long],
        (json \ tagField8).asOpt[Long],
        (json \ tagField9).asOpt[Long],
        (json \ tagField10).asOpt[Long],
        (json \ tagField11).asOpt[Long],
        (json \ tagField12).asOpt[Long],
        (json \ tagField13).asOpt[Long],
        (json \ tagField14).asOpt[Long],
        (json \ tagField15).asOpt[Long],
        (json \ tagField16).asOpt[Long],
        (json \ tagField17).asOpt[Long],
        (json \ tagField18).asOpt[Long],
        (json \ tagField19).asOpt[String],
        (json \ tagField20).asOpt[Long],
        (json \ tagField21).asOpt[Long],
        (json \ tagField22).asOpt[String],
        (json \ tagField23).asOpt[String],
        (json \ tagField24).asOpt[Long],
        (json \ tagField25).asOpt[Long],
        (json \ tagField26).asOpt[Long],
        (json \ tagField27).asOpt[Long],
        (json \ tagField28).asOpt[String],
        (json \ tagField29).asOpt[Long],
        (json \ tagField30).asOpt[Map[String, String]]
      )
    )

    override def writes(w: WindowPEOptionalHeaderType): JsValue = Json.obj(
      tagField1 -> w.magic_hex,
      tagField2 -> w.major_linker_version,
      tagField3 -> w.minor_linker_version,
      tagField4 -> w.size_of_code,
      tagField5 -> w.size_of_initialized_data,
      tagField6 -> w.size_of_uninitialized_data,
      tagField7 -> w.address_of_entry_point,
      tagField8 -> w.base_of_code,
      tagField9 -> w.base_of_data,
      tagField10 -> w.image_base,
      tagField11 -> w.section_alignment,
      tagField12 -> w.file_alignment,
      tagField13 -> w.major_os_version,
      tagField14 -> w.minor_os_version,
      tagField15 -> w.major_image_version,
      tagField16 -> w.minor_image_version,
      tagField17 -> w.major_subsystem_version,
      tagField18 -> w.minor_subsystem_version,
      tagField19 -> w.win32_version_value_hex,
      tagField20 -> w.size_of_image,
      tagField21 -> w.size_of_headers,
      tagField22 -> w.checksum_hex,
      tagField23 -> w.dll_characteristics_hex,
      tagField24 -> w.size_of_stack_reserve,
      tagField25 -> w.size_of_stack_commit,
      tagField26 -> w.size_of_heap_reserve,
      tagField27 -> w.size_of_heap_commit,
      tagField28 -> w.loader_flags_hex,
      tagField29 -> w.number_of_rva_and_sizes,
      tagField30 -> w.hashes
    )
  }

}

/**
  * The Windows PE Section type specifies metadata about a PE file section.
  */
case class WindowPESectionType(name: String,
                               size: Option[Long] = None,
                               entropy: Option[Float] = None,
                               hashes: Option[Map[String, String]] = None)

object WindowPESectionType {
  implicit val fmt = Json.format[WindowPESectionType]
}

/**
  * The Windows™ PE Binary File extension specifies a default extension for capturing properties specific to Windows portable executable (PE) files.
  */
case class WindowPEBinExt(pe_type: String,
                          imphash: Option[String] = None,
                          machine_hex: Option[String] = None,
                          number_of_sections: Option[Long] = None,
                          time_date_stamp: Option[Timestamp] = None,
                          pointer_to_symbol_table_hex: Option[String] = None,
                          number_of_symbols: Option[Long] = None,
                          size_of_optional_header: Option[Long] = None,
                          characteristics_hex: Option[String] = None,
                          file_header_hashes: Option[Map[String, String]] = None,
                          optional_header: Option[WindowPEOptionalHeaderType] = None,
                          sections: Option[List[WindowPESectionType]] = None) extends Extension

object WindowPEBinExt {
  val `type` = "windows-pebinary-ext"
  implicit val fmt = Json.format[WindowPEBinExt]
}


/**
  * The HTTP request extension specifies a default extension for capturing network traffic properties specific to HTTP requests.
  */
case class HttpRequestExt(request_method: String,
                          request_value: String,
                          request_version: Option[String] = None,
                          request_header: Option[Map[String, String]] = None,
                          message_body_length: Option[Long] = None,
                          message_body_data_ref: Option[String] = None) extends Extension

object HttpRequestExt {
  val `type` = "http-request-ext"
  implicit val fmt = Json.format[HttpRequestExt]
}

/**
  * The ICMP extension specifies a default extension for capturing network traffic properties specific to ICMP.
  */
case class ICMPExt(icmp_type_hex: String,
                   icmp_code_hex: String) extends Extension

object ICMPExt {
  val `type` = "icmp-ext"
  implicit val fmt = Json.format[ICMPExt]
}

/**
  * The TCP extension specifies a default extension for capturing network traffic properties specific to TCP.
  */
case class TCPExt(src_flags_hex: Option[String] = None,
                  dst_flags_hex: Option[String] = None) extends Extension

object TCPExt {
  val `type` = "tcp-ext"
  implicit val fmt = Json.format[TCPExt]
}

/**
  * The Network Socket extension specifies a default extension for capturing network traffic properties associated with network sockets.
  */
case class SocketExt(address_family: String,
                     is_blocking: Option[Boolean] = None,
                     is_listening: Option[Boolean] = None,
                     protocol_family: Option[String] = None,
                     options: Option[Map[String, String]] = None,
                     socket_type: Option[String] = None,
                     socket_descriptor: Option[Long] = None,
                     socket_handle: Option[Long] = None) extends Extension

object SocketExt {
  val `type` = "socket-ext"
  implicit val fmt = Json.format[SocketExt]
}

/**
  * The Windows Process extension specifies a default extension for capturing properties specific to Windows processes.
  */
case class WindowsProcessExt(aslr_enabled: Option[Boolean] = None,
                             dep_enabled: Option[Boolean] = None,
                             priority: Option[String] = None,
                             owner_sid: Option[String] = None,
                             window_title: Option[String] = None,
                             startup_info: Option[Map[String, String]] = None) extends Extension

object WindowsProcessExt {
  val `type` = "windows-process-ext"
  implicit val fmt = Json.format[WindowsProcessExt]
}

/**
  * The Windows Service extension specifies a default extension for capturing properties specific to Windows services.
  */
case class WindowsServiceExt(service_name: String,
                             descriptions: Option[List[String]] = None,
                             display_name: Option[String] = None,
                             group_name: Option[String] = None,
                             start_type: Option[String] = None,
                             service_dll_refs: Option[List[String]] = None,
                             service_type: Option[String] = None,
                             service_status: Option[String] = None) extends Extension

object WindowsServiceExt {
  val `type` = "windows-service-ext"
  implicit val fmt = Json.format[WindowsServiceExt]
}

/**
  * The UNIX account extension specifies a default extension for capturing the additional information for an account on a UNIX system.
  */
case class UnixAccountExt(gid: Option[Long] = None,
                          groups: Option[List[String]] = None,
                          home_dir: Option[String] = None,
                          shell: Option[String] = None) extends Extension

object UnixAccountExt {
  val `type` = "unix-account-ext"
  implicit val fmt = Json.format[UnixAccountExt]
}

/**
  * The X.509 v3 Extensions type captures properties associated with X.509 v3 extensions, which serve as a mechanism for
  * specifying additional information such as alternative subject names.
  */
case class X509V3Ext(basic_constraints: Option[String] = None,
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
                     inhibit_any_policy: Option[String] = None,
                     private_key_usage_period_not_before: Option[Timestamp] = None,
                     private_key_usage_period_not_after: Option[Timestamp] = None,
                     certificate_policies: Option[String] = None,
                     policy_mappings: Option[String] = None) extends Extension

object X509V3Ext {
  val `type` = "x509-v3-extensions-type"
  implicit val fmt = Json.format[X509V3Ext]
}

/**
  * represents a list of Predefined Cyber Observable Object Extension.
  */
case class Extensions(extensions: Map[String, Extension])

object Extensions {

  val theReads = new Reads[Extensions] {
    def reads(js: JsValue): JsResult[Extensions] = {
      val xMap = scala.collection.mutable.Map[String, Extension]()
      val theMap = js.as[Map[String, JsValue]]
      for ((k, v) <- theMap) {
        val res = k match {
          case ArchiveFileExt.`type` => ArchiveFileExt.fmt.reads(v)
          case NTFSFileExt.`type` => NTFSFileExt.fmt.reads(v)
          case PdfFileExt.`type` => PdfFileExt.fmt.reads(v)
          case RasterImgExt.`type` => RasterImgExt.fmt.reads(v)
          case WindowPEBinExt.`type` => WindowPEBinExt.fmt.reads(v)

          case HttpRequestExt.`type` => HttpRequestExt.fmt.reads(v)
          case ICMPExt.`type` => ICMPExt.fmt.reads(v)
          case TCPExt.`type` => TCPExt.fmt.reads(v)
          case SocketExt.`type` => SocketExt.fmt.reads(v)

          case WindowsProcessExt.`type` => WindowsProcessExt.fmt.reads(v)
          case WindowsServiceExt.`type` => WindowsServiceExt.fmt.reads(v)
          case UnixAccountExt.`type` => UnixAccountExt.fmt.reads(v)
          case X509V3Ext.`type` => X509V3Ext.fmt.reads(v)

          case x => CustomExtension.fmt.reads(v)
        }
        res.map(x => xMap += (k -> x.asInstanceOf[Extension]))
      }
      JsSuccess(Extensions(xMap.toMap))
    }
  }

  val theWrites = new Writes[Extensions] {
    def writes(obj: Extensions): JsValue = {
      val xMap = scala.collection.mutable.Map[String, JsValue]()
      for ((k, v) <- obj.extensions) {
        val res = k match {
          case ArchiveFileExt.`type` => ArchiveFileExt.fmt.writes(v.asInstanceOf[ArchiveFileExt])
          case NTFSFileExt.`type` => NTFSFileExt.fmt.writes(v.asInstanceOf[NTFSFileExt])
          case PdfFileExt.`type` => PdfFileExt.fmt.writes(v.asInstanceOf[PdfFileExt])
          case RasterImgExt.`type` => RasterImgExt.fmt.writes(v.asInstanceOf[RasterImgExt])
          case WindowPEBinExt.`type` => WindowPEBinExt.fmt.writes(v.asInstanceOf[WindowPEBinExt])

          case HttpRequestExt.`type` => HttpRequestExt.fmt.writes(v.asInstanceOf[HttpRequestExt])
          case ICMPExt.`type` => ICMPExt.fmt.writes(v.asInstanceOf[ICMPExt])
          case TCPExt.`type` => TCPExt.fmt.writes(v.asInstanceOf[TCPExt])
          case SocketExt.`type` => SocketExt.fmt.writes(v.asInstanceOf[SocketExt])

          case WindowsProcessExt.`type` => WindowsProcessExt.fmt.writes(v.asInstanceOf[WindowsProcessExt])
          case WindowsServiceExt.`type` => WindowsServiceExt.fmt.writes(v.asInstanceOf[WindowsServiceExt])
          case UnixAccountExt.`type` => UnixAccountExt.fmt.writes(v.asInstanceOf[UnixAccountExt])
          case X509V3Ext.`type` => X509V3Ext.fmt.writes(v.asInstanceOf[X509V3Ext])

          case _ => CustomExtension.fmt.writes(v.asInstanceOf[CustomExtension])
        }
        xMap += (k -> res)
      }
      JsObject(xMap)
    }
  }

  implicit val fmt: Format[Extensions] = Format(theReads, theWrites)
}


