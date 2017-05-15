package com.kodekutters.stix

import io.circe.{Decoder, Encoder, HCursor, Json}

/**
  *
  */
object Util {

  // list of all type names
  val StixObjTypes = Seq(AttackPattern.`type`, Identity.`type`, Campaign.`type`, CourseOfAction.`type`, Indicator.`type`,
    IntrusionSet.`type`, Malware.`type`, ObservedData.`type`, Report.`type`, ThreatActor.`type`,
    Tool.`type`, Vulnerability.`type`, Relationship.`type`, Sighting.`type`, MarkingDefinition.`type`,
    KillChainPhase.`type`)



  /**
    * convenience method to write as json a primitive value or collection of such
    *
    * @param s the primitive value to write as json
    */
/*
  implicit val encodeHashesType: Encoder[HashesType] = (hash: HashesType) => {
    val theList = for {h <- hash.kvList} yield h._1 -> Json.fromString(h._2)
    Json.obj(theList: _*)
  }

  implicit val decodeHashesType: Decoder[HashesType] = (c: HCursor) =>
    for {s <- c.value.as[List[Tuple2[String, String]]]} yield new HashesType(s)

    implicit val encodeTimestampq: Encoder[Timestamp] = new Encoder[Timestamp] {
      final def apply(a: Timestamp): Json = a.time.asJson
    }
    implicit val decodeTimestamp: Decoder[Timestamp] = new Decoder[Timestamp] {
      final def apply(c: HCursor): Decoder.Result[Timestamp] =
        for {
          s <- c.value.as[String]
        } yield new Timestamp(s)
    }

  def basicEncoder(s: Any): JsValue = {
    s match {
      case z: String => JsString(z)
      case z: Int => JsNumber(z)
      case z: Long => JsNumber(z)
      case z: Double => JsNumber(z)
      case z: Float => JsNumber(z.toDouble)
      case z: Byte => JsNumber(z.toInt)
      case z: Short => JsNumber(z.toInt)
      case z: Boolean => JsBoolean(z)
      case z: BigDecimal => JsNumber(z)
      case z: scala.collection.mutable.Traversable[_] => JsArray(for (s <- z.toSeq) yield basicWrite(s))
      case z: scala.collection.immutable.Traversable[_] => JsArray(for (s <- z.toSeq) yield basicWrite(s))
      case z => JsNull
    }
  }

  /**
    * convenience method to read a json value into a primitive value
    *
    * @param s the json value to read
    */
  private[this] def basicRead(s: JsValue): Any = {
    s match {
      case JsString(z) => z
      case JsNumber(z) => z.toDouble
      case JsBoolean(z) => z
      case JsArray(arr) => for (e <- arr) yield basicRead(e) // recursion
      case _ => null // includes the case JsNull
    }
  }
*/



}
