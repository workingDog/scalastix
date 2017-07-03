package com.kodekutters.stix

import play.api.libs.json._
import play.api.libs.functional.syntax._

import Util._

/**
  * represents a Predefined Cyber Observable Object Extension.
  * To create a Custom Object Extension, simply extends this trait
  */
trait Extension {
  val `type`: String
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
case class AlternateDataStream(`type`: String = AlternateDataStream.`type`,
                               name: String,
                               hashes: Option[Map[String, String]] = None,
                               size: Option[Int] = None)

object AlternateDataStream {
  val `type` = "alternate-data-stream"
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
                        image_height: Option[Int] = None,
                        image_width: Option[Int] = None,
                        bits_per_pixel: Option[Int] = None,
                        image_compression_algorithm: Option[String] = None,
                        exif_tags: Option[Map[String, Either[Int, String]]]) extends Extension

object RasterImgExt {
  val `type` = "raster-image-ext"
  implicit val fmt = Json.format[RasterImgExt]
}

/**
  * The Windows PE Optional Header type represents the properties of the PE optional header.
  */
case class WindowPEOptionalHeaderType(`type`: String = WindowPEOptionalHeaderType.`type`,
                                      magic_hex: Option[String] = None,
                                      major_linker_version: Option[Int] = None,
                                      minor_linker_version: Option[Int] = None,
                                      size_of_code: Option[Int] = None,
                                      size_of_initialized_data: Option[Int] = None,
                                      size_of_uninitialized_data: Option[Int] = None,
                                      address_of_entry_point: Option[Int] = None,
                                      base_of_code: Option[Int] = None,
                                      base_of_data: Option[Int] = None,
                                      image_base: Option[Int] = None,
                                      section_alignment: Option[Int] = None,
                                      file_alignment: Option[Int] = None,
                                      major_os_version: Option[Int] = None,
                                      minor_os_version: Option[Int] = None,
                                      major_image_version: Option[Int] = None,
                                      minor_image_version: Option[Int] = None,
                                      major_subsystem_version: Option[Int] = None,
                                      minor_subsystem_version: Option[Int] = None,
                                      win32_version_value_hex: Option[String] = None,
                                      size_of_image: Option[Int] = None,
                                      size_of_headers: Option[Int] = None,
                                      checksum_hex: Option[String] = None,
                                      dll_characteristics_hex: Option[String] = None,
                                      size_of_stack_reserve: Option[Int] = None,
                                      size_of_stack_commit: Option[Int] = None,
                                      size_of_heap_reserve: Option[Int] = None,
                                      size_of_heap_commit: Option[Int] = None,
                                      loader_flags_hex: Option[String] = None,
                                      number_of_rva_and_sizes: Option[Int] = None,
                                      hashes: Option[Map[String, String]] = None)

object WindowPEOptionalHeaderType {
  val `type` = "windows-pe-optional-header-type"

  val part1: OFormat[(String, Option[String], Option[Int], Option[Int], Option[Int], Option[Int],
    Option[Int], Option[Int], Option[Int], Option[Int], Option[Int], Option[Int], Option[Int],
    Option[Int], Option[Int])] =
    ((__ \ "type").format[String] ~
      (__ \ "magic_hex").formatNullable[String] ~
      (__ \ "major_linker_version").formatNullable[Int] ~
      (__ \ "minor_linker_version").formatNullable[Int] ~
      (__ \ "size_of_code").formatNullable[Int] ~
      (__ \ "size_of_initialized_data").formatNullable[Int] ~
      (__ \ "size_of_uninitialized_data").formatNullable[Int] ~
      (__ \ "address_of_entry_point").formatNullable[Int] ~
      (__ \ "base_of_code").formatNullable[Int] ~
      (__ \ "base_of_data").formatNullable[Int] ~
      (__ \ "image_base").formatNullable[Int] ~
      (__ \ "section_alignment").formatNullable[Int] ~
      (__ \ "file_alignment").formatNullable[Int] ~
      (__ \ "major_os_version").formatNullable[Int] ~
      (__ \ "minor_os_version").formatNullable[Int]).tupled

  val part2: OFormat[(Option[Int], Option[Int], Option[Int], Option[Int], Option[String], Option[Int],
    Option[Int], Option[String], Option[String], Option[Int], Option[Int], Option[Int], Option[Int],
    Option[String], Option[Int], Option[Map[String, String]])] =
    ((__ \ "major_image_version").formatNullable[Int] ~
      (__ \ "minor_image_version").formatNullable[Int] ~
      (__ \ "major_subsystem_version").formatNullable[Int] ~
      (__ \ "minor_subsystem_version").formatNullable[Int] ~
      (__ \ "win32_version_value_hex").formatNullable[String] ~
      (__ \ "size_of_image").formatNullable[Int] ~
      (__ \ "size_of_headers").formatNullable[Int] ~
      (__ \ "checksum_hex").formatNullable[String] ~
      (__ \ "dll_characteristics_hex").formatNullable[String] ~
      (__ \ "size_of_stack_reserve").formatNullable[Int] ~
      (__ \ "size_of_stack_commit").formatNullable[Int] ~
      (__ \ "size_of_heap_reserve").formatNullable[Int] ~
      (__ \ "size_of_heap_commit").formatNullable[Int] ~
      (__ \ "loader_flags_hex").formatNullable[String] ~
      (__ \ "number_of_rva_and_sizes").formatNullable[Int] ~
      (__ \ "hashes").formatNullable[Map[String, String]]
      ).tupled

  implicit val fmt: Format[WindowPEOptionalHeaderType] = (part1 ~ part2) ({
    case ((`type`, magic_hex, major_linker_version, minor_linker_version, size_of_code,
    size_of_initialized_data, size_of_uninitialized_data, address_of_entry_point, base_of_code,
    base_of_data, image_base, section_alignment, file_alignment, major_os_version, minor_os_version),
    (major_image_version, minor_image_version, major_subsystem_version, minor_subsystem_version, win32_version_value_hex,
    size_of_image, size_of_headers, checksum_hex, dll_characteristics_hex, size_of_stack_reserve, size_of_stack_commit,
    size_of_heap_reserve, size_of_heap_commit, loader_flags_hex, number_of_rva_and_sizes, hashes)) =>
      new WindowPEOptionalHeaderType(`type`, magic_hex, major_linker_version, minor_linker_version, size_of_code,
        size_of_initialized_data, size_of_uninitialized_data, address_of_entry_point, base_of_code,
        base_of_data, image_base, section_alignment, file_alignment, major_os_version, minor_os_version,
        major_image_version, minor_image_version, major_subsystem_version, minor_subsystem_version, win32_version_value_hex,
        size_of_image, size_of_headers, checksum_hex, dll_characteristics_hex, size_of_stack_reserve, size_of_stack_commit,
        size_of_heap_reserve, size_of_heap_commit, loader_flags_hex, number_of_rva_and_sizes, hashes)
  }, (t: WindowPEOptionalHeaderType) => ((t.`type`, t.magic_hex, t.major_linker_version, t.minor_linker_version, t.size_of_code,
    t.size_of_initialized_data, t.size_of_uninitialized_data, t.address_of_entry_point, t.base_of_code,
    t.base_of_data, t.image_base, t.section_alignment, t.file_alignment, t.major_os_version, t.minor_os_version),(
    t.major_image_version, t.minor_image_version, t.major_subsystem_version, t.minor_subsystem_version, t.win32_version_value_hex,
    t.size_of_image, t.size_of_headers, t.checksum_hex, t.dll_characteristics_hex, t.size_of_stack_reserve, t.size_of_stack_commit,
    t.size_of_heap_reserve, t.size_of_heap_commit, t.loader_flags_hex, t.number_of_rva_and_sizes, t.hashes)))
}

/**
  * The Windows PE Section type specifies metadata about a PE file section.
  */
case class WindowPESectionType(`type`: String = WindowPESectionType.`type`,
                               name: String,
                               size: Option[Int] = None,
                               entropy: Option[Float] = None,
                               hashes: Option[Map[String, String]] = None)

object WindowPESectionType {
  val `type` = "windows-pe-section"
  implicit val fmt = Json.format[WindowPESectionType]
}

/**
  * The Windows™ PE Binary File extension specifies a default extension for capturing properties specific to Windows portable executable (PE) files.
  */
case class WindowPEBinExt(`type`: String = WindowPEBinExt.`type`,
                          pe_type: String,
                          imphash: Option[String] = None,
                          machine_hex: Option[String] = None,
                          number_of_sections: Option[Int] = None,
                          time_date_stamp: Option[Timestamp] = None,
                          pointer_to_symbol_table_hex: Option[String] = None,
                          number_of_symbols: Option[Int] = None,
                          size_of_optional_header: Option[Int] = None,
                          characteristics_hex: Option[String] = None,
                          file_header_hashes: Option[Map[String, String]] = None,
                          optional_header: Option[WindowPEOptionalHeaderType] = None,
                          sections: Option[List[WindowPESectionType]] = None) extends Extension

object WindowPEBinExt {
  val `type` = "windows-pebinary-ext"
  implicit val fmt = Json.format[WindowPEBinExt]
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
        // todo ---> custom Extensions
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
        // todo ---> custom Extensions
        case _ => JsNull
      }
    }
  }

  implicit val fmt: Format[Extension] = Format(theReads, theWrites)
}
