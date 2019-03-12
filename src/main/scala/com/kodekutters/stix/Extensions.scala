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
trait Extension {
  val `type`: String
}

/**
  * a generic unknown custom extension object
  */
case class CustomExtension(`type`: String, custom: Option[CustomProps] = None) extends Extension

object CustomExtension {
  implicit val fmt = Json.format[CustomExtension]
}

/**
  * The Archive File extension specifies a default extension for capturing properties specific to archive files.
  */
case class ArchiveFileExt(`type`: String = ArchiveFileExt.`type`,
                          contains_refs: Option[List[String]] = None,
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
case class NTFSFileExt(`type`: String = NTFSFileExt.`type`,
                       sid: Option[String] = None,
                       alternate_data_streams: Option[List[AlternateDataStream]] = None) extends Extension

object NTFSFileExt {
  val `type` = "ntfs-ext"
  implicit val fmt = Json.format[NTFSFileExt]
}

/**
  * The PDF file extension specifies a default extension for capturing properties specific to PDF files.
  */
case class PdfFileExt(`type`: String = PdfFileExt.`type`,
                      version: Option[String] = None,
                      is_optimized: Option[Boolean] = None,
                      pdfid0: Option[String] = None,
                      pdfid1: Option[String] = None) extends Extension

object PdfFileExt {
  val `type` = "pdf-ext"
  implicit val fmt = Json.format[PdfFileExt]
}

/**
  * The Raster Image file extension specifies a default extension for capturing properties specific to raster image files.
  */
case class RasterImgExt(`type`: String = RasterImgExt.`type`,
                        image_height: Option[Long] = None,
                        image_width: Option[Long] = None,
                        bits_per_pixel: Option[Long] = None,
                        image_compression_algorithm: Option[String] = None,
                        exif_tags: Option[Map[String, Either[Long, String]]]) extends Extension

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

  val part1: OFormat[(Option[String], Option[Long], Option[Long], Option[Long], Option[Long],
    Option[Long], Option[Long], Option[Long], Option[Long], Option[Long], Option[Long], Option[Long],
    Option[Long], Option[Long])] =
    ((__ \ "magic_hex").formatNullable[String] ~
      (__ \ "major_linker_version").formatNullable[Long] ~
      (__ \ "minor_linker_version").formatNullable[Long] ~
      (__ \ "size_of_code").formatNullable[Long] ~
      (__ \ "size_of_initialized_data").formatNullable[Long] ~
      (__ \ "size_of_uninitialized_data").formatNullable[Long] ~
      (__ \ "address_of_entry_point").formatNullable[Long] ~
      (__ \ "base_of_code").formatNullable[Long] ~
      (__ \ "base_of_data").formatNullable[Long] ~
      (__ \ "image_base").formatNullable[Long] ~
      (__ \ "section_alignment").formatNullable[Long] ~
      (__ \ "file_alignment").formatNullable[Long] ~
      (__ \ "major_os_version").formatNullable[Long] ~
      (__ \ "minor_os_version").formatNullable[Long]).tupled

  val part2: OFormat[(Option[Long], Option[Long], Option[Long], Option[Long], Option[String], Option[Long],
    Option[Long], Option[String], Option[String], Option[Long], Option[Long], Option[Long], Option[Long],
    Option[String], Option[Long], Option[Map[String, String]])] =
    ((__ \ "major_image_version").formatNullable[Long] ~
      (__ \ "minor_image_version").formatNullable[Long] ~
      (__ \ "major_subsystem_version").formatNullable[Long] ~
      (__ \ "minor_subsystem_version").formatNullable[Long] ~
      (__ \ "win32_version_value_hex").formatNullable[String] ~
      (__ \ "size_of_image").formatNullable[Long] ~
      (__ \ "size_of_headers").formatNullable[Long] ~
      (__ \ "checksum_hex").formatNullable[String] ~
      (__ \ "dll_characteristics_hex").formatNullable[String] ~
      (__ \ "size_of_stack_reserve").formatNullable[Long] ~
      (__ \ "size_of_stack_commit").formatNullable[Long] ~
      (__ \ "size_of_heap_reserve").formatNullable[Long] ~
      (__ \ "size_of_heap_commit").formatNullable[Long] ~
      (__ \ "loader_flags_hex").formatNullable[String] ~
      (__ \ "number_of_rva_and_sizes").formatNullable[Long] ~
      (__ \ "hashes").formatNullable[Map[String, String]]
      ).tupled

  implicit val fmt: Format[WindowPEOptionalHeaderType] = (part1 ~ part2) ({
    case ((magic_hex, major_linker_version, minor_linker_version, size_of_code,
    size_of_initialized_data, size_of_uninitialized_data, address_of_entry_point, base_of_code,
    base_of_data, image_base, section_alignment, file_alignment, major_os_version, minor_os_version),
    (major_image_version, minor_image_version, major_subsystem_version, minor_subsystem_version, win32_version_value_hex,
    size_of_image, size_of_headers, checksum_hex, dll_characteristics_hex, size_of_stack_reserve, size_of_stack_commit,
    size_of_heap_reserve, size_of_heap_commit, loader_flags_hex, number_of_rva_and_sizes, hashes)) =>
      new WindowPEOptionalHeaderType(magic_hex, major_linker_version, minor_linker_version, size_of_code,
        size_of_initialized_data, size_of_uninitialized_data, address_of_entry_point, base_of_code,
        base_of_data, image_base, section_alignment, file_alignment, major_os_version, minor_os_version,
        major_image_version, minor_image_version, major_subsystem_version, minor_subsystem_version, win32_version_value_hex,
        size_of_image, size_of_headers, checksum_hex, dll_characteristics_hex, size_of_stack_reserve, size_of_stack_commit,
        size_of_heap_reserve, size_of_heap_commit, loader_flags_hex, number_of_rva_and_sizes, hashes)
  }, (t: WindowPEOptionalHeaderType) => ((t.magic_hex, t.major_linker_version, t.minor_linker_version, t.size_of_code,
    t.size_of_initialized_data, t.size_of_uninitialized_data, t.address_of_entry_point, t.base_of_code,
    t.base_of_data, t.image_base, t.section_alignment, t.file_alignment, t.major_os_version, t.minor_os_version),(
    t.major_image_version, t.minor_image_version, t.major_subsystem_version, t.minor_subsystem_version, t.win32_version_value_hex,
    t.size_of_image, t.size_of_headers, t.checksum_hex, t.dll_characteristics_hex, t.size_of_stack_reserve, t.size_of_stack_commit,
    t.size_of_heap_reserve, t.size_of_heap_commit, t.loader_flags_hex, t.number_of_rva_and_sizes, t.hashes)))
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
case class WindowPEBinExt(`type`: String = WindowPEBinExt.`type`,
                          pe_type: String,
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
case class HttpRequestExt(`type`: String = HttpRequestExt.`type`,
                          request_method: String,
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
case class ICMPExt(`type`: String = ICMPExt.`type`,
                   icmp_type_hex: String,
                   icmp_code_hex: String) extends Extension

object ICMPExt {
  val `type` = "icmp-ext"
  implicit val fmt = Json.format[ICMPExt]
}

/**
  * The TCP extension specifies a default extension for capturing network traffic properties specific to TCP.
  */
case class TCPExt(`type`: String = TCPExt.`type`,
                  src_flags_hex: Option[String] = None,
                  dst_flags_hex: Option[String] = None) extends Extension

object TCPExt {
  val `type` = "tcp-ext"
  implicit val fmt = Json.format[TCPExt]
}

/**
  * The Network Socket extension specifies a default extension for capturing network traffic properties associated with network sockets.
  */
case class SocketExt(`type`: String = SocketExt.`type`,
                     address_family: String,
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
  * represents a Predefined Cyber Observable Object Extension
  */
object Extension {

  val theReads = new Reads[Extension] {
    def reads(js: JsValue): JsResult[Extension] = {
      (js \ "type").asOpt[String].map({
        case ArchiveFileExt.`type` => ArchiveFileExt.fmt.reads(js)
        case NTFSFileExt.`type` => NTFSFileExt.fmt.reads(js)
        case PdfFileExt.`type` => PdfFileExt.fmt.reads(js)
        case RasterImgExt.`type` => RasterImgExt.fmt.reads(js)
        case WindowPEBinExt.`type` => WindowPEBinExt.fmt.reads(js)

        case HttpRequestExt.`type` => HttpRequestExt.fmt.reads(js)
        case ICMPExt.`type` => ICMPExt.fmt.reads(js)
        case TCPExt.`type` => TCPExt.fmt.reads(js)
        case SocketExt.`type` => SocketExt.fmt.reads(js)

        case x => CustomExtension.fmt.reads(js)
      }).getOrElse(JsError("Error reading Extension"))
    }
  }

  val theWrites = new Writes[Extension] {
    def writes(obj: Extension) = {
      obj match {
        case ext: ArchiveFileExt => ArchiveFileExt.fmt.writes(ext)
        case ext: NTFSFileExt => NTFSFileExt.fmt.writes(ext)
        case ext: PdfFileExt => PdfFileExt.fmt.writes(ext)
        case ext: RasterImgExt => RasterImgExt.fmt.writes(ext)
        case ext: WindowPEBinExt => WindowPEBinExt.fmt.writes(ext)

        case ext: HttpRequestExt => HttpRequestExt.fmt.writes(ext)
        case ext: ICMPExt => ICMPExt.fmt.writes(ext)
        case ext: TCPExt => TCPExt.fmt.writes(ext)
        case ext: SocketExt => SocketExt.fmt.writes(ext)

        case ext: CustomExtension => CustomExtension.fmt.writes(ext)
        case _ => JsNull
      }
    }
  }

  implicit val fmt: Format[Extension] = Format(theReads, theWrites)
}
