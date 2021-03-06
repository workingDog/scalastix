package com.kodekutters.stix

import scala.language.implicitConversions

/**
  * a set of implicit conversions. Use with care.
  *
  */
object StixImplicits {

  // ------------------X to Option[X]--------------------------------------------------------

  implicit def StringToStringOp(value: String): Option[String] = Option(value)

  implicit def DoubleToDoubleOp(value: Double): Option[Double] = Option(value)

  implicit def LongToIntOp(value: Long): Option[Long] = Option(value)

  implicit def BoolToBoolOp(value: Boolean): Option[Boolean] = Option(value)

  implicit def KillChainPhaseToKillChainPhaseOp(value: KillChainPhase): Option[KillChainPhase] = Option(value)

  implicit def ExtRefToExtRefOp(value: List[ExternalReference]): Option[List[ExternalReference]] = Option(value)

  implicit def IdentifierToIdentifierOp(value: List[Identifier]): Option[List[Identifier]] = Option(value)

  implicit def GranularMarkingToGranularMarkingOp(value: List[GranularMarking]): Option[List[GranularMarking]] = Option(value)

  implicit def TimestampToTimestampOp(value: Timestamp): Option[Timestamp] = Option(value)

  implicit def ListOfStringToListOfStringOp(value: List[String]): Option[List[String]] = Option(value)

  implicit def ExtensionsToExtensionsOp(value: Extensions): Option[Extensions] = Option(value)

  implicit def ListOfAltDataStreamToOp(value: List[AlternateDataStream]): Option[List[AlternateDataStream]] = Option(value)

  implicit def MapStringToOp(value: Map[String, String]): Option[Map[String, String]] = Option(value)

  implicit def ListOfKillChainPhaseToOp(value: List[KillChainPhase]): Option[List[KillChainPhase]] = Option(value)

  implicit def CustomMapToOpt(value: CustomProps): Option[CustomProps] = Option(value)

  implicit def IdentifierToOpt(value: Identifier): Option[Identifier] = Option(value)

  // ---------------------------------------------------------------------------------------

}
