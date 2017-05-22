package com.kodekutters.stix

import io.circe.syntax._
import io.circe.{Json, _}
import io.circe.generic.auto._
import io.circe.Decoder._
import io.circe._

import scala.language.implicitConversions

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
}

/**
  * The NTFS file extension specifies a default extension for capturing properties specific to the storage of the file on the NTFS file system.
  */
case class NTFSFileExt(`type`: String = NTFSFileExt.`type`,
                       sid: Option[String] = None,
                       alternate_data_streams: Option[List[AlternateDataStream]] = None) extends Extension

object NTFSFileExt {
  val `type` = "ntfs-ext"
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
}

/**
  * The Windows PE Section type specifies metadata about a PE file section.
  */
case class WindowPESectionType(`type`: String = WindowPEOptionalHeaderType.`type`,
                               name: String,
                               size: Option[Int] = None,
                               entropy: Option[Float] = None,
                               hashes: Option[Map[String, String]] = None)

object WindowPESectionType {
  val `type` = "windows-pe-section"
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
}

/**
  * represents a Predefined Cyber Observable Object Extension
  */
object Extension {

  implicit val decodeExtension: Decoder[Extension] = Decoder.instance(c =>
    c.downField("type").as[String].right.flatMap {
      case ArchiveFileExt.`type` => c.as[ArchiveFileExt]
      case NTFSFileExt.`type` => c.as[NTFSFileExt]
      case PdfFileExt.`type` => c.as[PdfFileExt]
      case RasterImgExt.`type` => c.as[RasterImgExt]
      case WindowPEBinExt.`type` => c.as[WindowPEBinExt]
      //  case err => c.as[ExtensionError]
    })

  implicit val encodeExtension: Encoder[Extension] = new Encoder[Extension] {
    final def apply(ext: Extension): Json = {
      ext match {
        case s: ArchiveFileExt => ext.asInstanceOf[ArchiveFileExt].asJson
        case s: NTFSFileExt => ext.asInstanceOf[NTFSFileExt].asJson
        case s: PdfFileExt => ext.asInstanceOf[PdfFileExt].asJson
        case s: WindowPEBinExt => ext.asInstanceOf[WindowPEBinExt].asJson
        case _ => Json.Null
      }
    }
  }

}
