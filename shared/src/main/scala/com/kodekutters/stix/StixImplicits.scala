package com.kodekutters.stix

import play.extras.geojson.{GeoJson, LatLng}

import scala.language.implicitConversions

/**
  * a set of implicit conversions. Use with care.
  *
  */
object StixImplicits {

  // ------------------X to Option[X]--------------------------------------------------------

  implicit def StringToStringOp(value: String): Option[String] = Option(value)

  implicit def DoubleToDoubleOp(value: Double): Option[Double] = Option(value)

  implicit def IntToIntOp(value: Int): Option[Int] = Option(value)

  implicit def BoolToBoolOp(value: Boolean): Option[Boolean] = Option(value)

  implicit def KillChainPhaseToKillChainPhaseOp(value: KillChainPhase): Option[KillChainPhase] = Option(value)

  implicit def ExtRefToExtRefOp(value: List[ExternalReference]): Option[List[ExternalReference]] = Option(value)

  implicit def IdentifierToIdentifierOp(value: List[Identifier]): Option[List[Identifier]] = Option(value)

  implicit def GranularMarkingToGranularMarkingOp(value: List[GranularMarking]): Option[List[GranularMarking]] = Option(value)

  implicit def TimestampToTimestampOp(value: Timestamp): Option[Timestamp] = Option(value)

  implicit def ListOfStringToListOfStringOp(value: List[String]): Option[List[String]] = Option(value)

  implicit def ExtensionsToExtensionsOp(value: Map[String, Extension]): Option[Map[String, Extension]] = Option(value)

  implicit def ListOfAltDataStreamToOp(value: List[AlternateDataStream]): Option[List[AlternateDataStream]] = Option(value)

  implicit def MapStringToOp(value: Map[String, String]): Option[Map[String, String]] = Option(value)

  implicit def ListOfKillChainPhaseToOp(value: List[KillChainPhase]): Option[List[KillChainPhase]] = Option(value)

  implicit def AddressToOpt(value: Address): Option[Address] = Option(value)

  implicit def LocationToOpt(value: Location): Option[Location] = Option(value)

  implicit def GeojsonToOpt(value: GeoJson[LatLng]): Option[GeoJson[LatLng]] = Option(value)

  // ---------------------------------------------------------------------------------------



}
