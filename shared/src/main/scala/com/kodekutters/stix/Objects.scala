package com.kodekutters.stix

import org.threeten.bp._
//import play.extras.geojson._
import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._
import Util._

/**
  * STIX-2.1 protocol
  *
  * https://docs.google.com/document/d/1nipwFIaFwkHo4Gzw-qxZQpCjP_5tX7rbI3Ic5C56Z88/edit
  *
  * Author: R. Wathelet May 2017
  */

//-----------------------------------------------------------------------
//------------------supporting data types--------------------------------
//-----------------------------------------------------------------------

/**
  * a valid RFC 3339-formatted timestamp [RFC3339] using the format YYYY-MM-DDTHH:mm:ss[.s+]Z
  * where the “s+” represents 1 or more sub-second values.
  *
  * @param time the time formatted as YYYY-MM-DDTHH:mm:ss[.s+]Z
  */
case class Timestamp(time: String) {
  val `type` = Timestamp.`type`

  override def toString: String = time.toString
}

object Timestamp {
  val `type` = "timestamp"

  val theReads = new Reads[Timestamp] {
    def reads(js: JsValue): JsResult[Timestamp] = {
      JsSuccess(new Timestamp(js.as[String]))
    }
  }

  val theWrites = new Writes[Timestamp] {
    def writes(obj: Timestamp) = {
      JsString(obj.time)
    }
  }

  implicit val fmt: Format[Timestamp] = Format(theReads, theWrites)

  def now() = new Timestamp(ZonedDateTime.now(ZoneId.of("Z")).toString)
}

/**
  * An identifier universally and uniquely identifies a SDO, SRO, Bundle, or Marking Definition.
  *
  * @param objType the type property of the object being identified or referenced
  * @param id      an RFC 4122-compliant Version 4 UUID as a string
  */
case class Identifier(objType: String, id: String) {
  val `type` = Identifier.`type`

  override def toString = objType + "--" + id
}

object Identifier {
  val `type` = "identifier"

  val theReads = new Reads[Identifier] {
    def reads(js: JsValue): JsResult[Identifier] = {
      JsSuccess(stringToIdentifier(js.as[String]))
    }
  }

  val theWrites = new Writes[Identifier] {
    def writes(obj: Identifier) = {
      JsString(obj.toString)
    }
  }

  implicit val fmt: Format[Identifier] = Format(theReads, theWrites)

  def stringToIdentifier(s: String): Identifier = {
    val part = s.split("--")
    new Identifier(part(0), part(1))
  }

  def apply(objType: String) = new Identifier(objType, UUID.randomUUID().toString)
}

/**
  * The kill-chain-phase represents a phase in a kill chain, which describes the various phases
  * an attacker may undertake in order to achieve their objectives.
  */
case class KillChainPhase(kill_chain_name: String, phase_name: String) {
  val `type` = KillChainPhase.`type`

  override def toString = kill_chain_name + "," + phase_name
}

object KillChainPhase {
  val `type` = "kill-chain-phase"

  implicit val fmt = Json.format[KillChainPhase]
}

//-----------------------------------------------------------------------
//------------------Marking----------------------------------------------
//-----------------------------------------------------------------------

trait MarkingObject

/**
  * TLP levels
  */
sealed case class TLPlevels(value: String)

object TLPlevels {

  object red extends TLPlevels("red")

  object amber extends TLPlevels("amber")

  object green extends TLPlevels("green")

  object white extends TLPlevels("white")

  val values = Seq(red, amber, green, white)

  def fromString(s: String): TLPlevels = {
    s match {
      case red.value => new TLPlevels(red.value.toString)
      case amber.value => new TLPlevels(amber.value.toString)
      case green.value => new TLPlevels(green.value.toString)
      case white.value => new TLPlevels(white.value.toString)
      case x => new TLPlevels(x) // todo is this correct <------
    }
  }

  val theReads = new Reads[TLPlevels] {
    def reads(js: JsValue): JsResult[TLPlevels] = {
      JsSuccess(TLPlevels.fromString(js.as[String]))
    }
  }

  val theWrites = new Writes[TLPlevels] {
    def writes(obj: TLPlevels) = {
      JsString(obj.value.toString)
    }
  }

  implicit val fmt: Format[TLPlevels] = Format(theReads, theWrites)
}

/**
  * The TLP marking type defines how you would represent a Traffic Light Protocol (TLP) marking in a definition property.
  *
  * @param tlp the tlp level MUST be one of white, green, red, amber
  */
case class TPLMarking(tlp: TLPlevels) extends MarkingObject

object TPLMarking {
  implicit val fmt = Json.format[TPLMarking]
}

/**
  * The Statement marking type defines the representation of a textual marking statement
  * (e.g., copyright, terms of use, etc.) in a definition.
  *
  * @param statement the statement string
  */
case class StatementMarking(statement: String) extends MarkingObject

object StatementMarking {
  implicit val fmt = Json.format[StatementMarking]
}

/**
  * Marking Object, TPLMarking or StatementMarking
  */
object MarkingObject {

  val theReads = new Reads[MarkingObject] {
    def reads(js: JsValue): JsResult[MarkingObject] = {
      StatementMarking.fmt.reads(js) | TPLMarking.fmt.reads(js)
    }
  }

  val theWrites = new Writes[MarkingObject] {
    def writes(obj: MarkingObject) = {
      obj match {
        case s: TPLMarking => TPLMarking.fmt.writes(s)
        case s: StatementMarking => StatementMarking.fmt.writes(s)
        case _ => JsNull
      }
    }
  }

  implicit val fmt: Format[MarkingObject] = Format(theReads, theWrites)
}

/**
  * granular markings allow data markings to be applied to individual portions of STIX Objects
  * and Marking Definitions.
  */
case class GranularMarking(selectors: List[String], marking_ref: Option[String] = None, lang: Option[String] = None) {

  val `type` = GranularMarking.`type`

  override def toString = {
    val sb1 = new StringBuilder()
    for (s <- selectors) sb1.append(s + ";")
    // remove the last ";"
    val sb = new StringBuilder(sb1.toString().reverse.substring(1).reverse)
    sb.append(marking_ref.getOrElse(""))
    sb.append(",")
    sb.append(lang.getOrElse(""))
    sb.toString()
  }

}

object GranularMarking {
  val `type` = "granular-marking"

  implicit val fmt = Json.format[GranularMarking]
}

/**
  * External references are used to describe pointers to information represented outside of STIX.
  */
case class ExternalReference(source_name: String, description: Option[String] = None, url: Option[String] = None, external_id: Option[String] = None) {
  val `type` = ExternalReference.`type`

  override def toString = source_name + "," + description.getOrElse("") + "," + url.getOrElse("") + "," + external_id.getOrElse("")
}

object ExternalReference {
  val `type` = "external-reference"

  implicit val fmt = Json.format[ExternalReference]
}

//-----------------------------------------------------------------------
//------------------x-custom support-------------------------------------
//-----------------------------------------------------------------------

/**
  * a generic custom (key,value) dictionary representing the Custom Properties,
  * with key = a custom property name, i.e. starting with "x_", and value = the property JsValue
  */
case class CustomProps(nodes: Map[String, JsValue])

object CustomProps {

  def readAttributes(js: JsValue, omitList: List[String]): Option[CustomProps] = {
    js match {
      case json: JsObject =>
        // get all fields of js, but not the fields in the omitList, this gives all the custom property fields
        val fList = json.fields.filterNot(p => omitList.contains(p._1))
        if (fList.isEmpty) None else Some(new CustomProps(fList.toMap))

      case x => JsError(s"Could not read custom field: $x"); None
    }
  }

  val theReads = new Reads[CustomProps] {
    def reads(json: JsValue): JsResult[CustomProps] = {
      json match {
        case js: JsObject => JsSuccess(new CustomProps(js.fields.toMap))
        case x => JsError(s"Error could not read custom: $x")
      }
    }
  }

  val theWrites = new Writes[CustomProps] {
    def writes(custom: CustomProps): JsObject = JsObject(custom.nodes)
  }

  implicit val fmt: Format[CustomProps] = Format(theReads, theWrites)
}

//-----------------------------------------------------------------------

/**
  * a general STIX object representing the SDOs, SROs, LanguageContent and MarkingDefinition
  */
trait StixObj {
  val `type`: String
  val id: Identifier
  val custom: Option[CustomProps] // the custom properties as a map of property names and values
}

/**
  * The marking-definition object represents a specific marking.
  */
case class MarkingDefinition(`type`: String = MarkingDefinition.`type`,
                             id: Identifier = Identifier(MarkingDefinition.`type`),
                             created: Timestamp = Timestamp.now(),
                             definition_type: String,
                             definition: MarkingObject,
                             external_references: Option[List[ExternalReference]] = None,
                             object_marking_refs: Option[List[Identifier]] = None,
                             granular_markings: Option[List[GranularMarking]] = None,
                             created_by_ref: Option[Identifier] = None,
                             custom: Option[CustomProps] = None) extends StixObj

object MarkingDefinition {
  val `type` = "marking-definition"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(MarkingDefinition(definition_type = "test", definition = StatementMarking("test")))

  val theReads = new Reads[MarkingDefinition] {
    def reads(js: JsValue): JsResult[MarkingDefinition] = {
      if ((js \ "type").asOpt[String].contains(MarkingDefinition.`type`)) {
        JsSuccess(new MarkingDefinition(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "definition_type").as[String],
          (js \ "definition").as[MarkingObject],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading MarkingDefinition: $js")
      }
    }
  }

  val theWrites = new Writes[MarkingDefinition] {
    def writes(p: MarkingDefinition): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "definition_type" -> JsString(p.definition_type),
        "definition" -> Json.toJson(p.definition))

      val theList = JsObject(List(
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[MarkingDefinition] = Format(theReads, theWrites)
}

//-----------------------------------------------------------------------
//------------------Address and Location---------------------------------
//-----------------------------------------------------------------------
/**
  * The Address is a sub-type used only by location and is used to describe civic (street) addresses.
  */
//case class Address(country: String,
//                   administrative_area: Option[String] = None,
//                   city: Option[String] = None,
//                   address: Option[String] = None,
//                   postal_code: Option[String] = None)
//
//object Address {
//  implicit val fmt = Json.format[Address]
//}

/**
  * Location is used to describe geographic locations.
  * It supports describing by general region, civic address, or using GeoJSON.
  */
//case class Location(region: String,
//                    address: Option[Address] = None,
//                    geojson: Option[GeoJson[LatLng]] = None)
//
//object Location {
//  implicit val fmt = Json.format[Location]
//}

//-----------------------------------------------------------------------
//------------------STIX Domain Objects----------------------------------
//-----------------------------------------------------------------------

/**
  * common properties of all SDO and SRO
  */
trait SDO extends StixObj {
  val created: Timestamp
  val modified: Timestamp
  val created_by_ref: Option[Identifier]
  val revoked: Option[Boolean]
  val labels: Option[List[String]]
  val confidence: Option[Int]
  val external_references: Option[List[ExternalReference]]
  val lang: Option[String]
  val object_marking_refs: Option[List[Identifier]]
  val granular_markings: Option[List[GranularMarking]]
}

/**
  * Attack Patterns are a type of TTP that describe ways that adversaries attempt to compromise targets.
  */
case class AttackPattern(`type`: String = AttackPattern.`type`,
                         id: Identifier = Identifier(AttackPattern.`type`),
                         created: Timestamp = Timestamp.now(),
                         modified: Timestamp = Timestamp.now(),
                         name: String,
                         description: Option[String] = None,
                         kill_chain_phases: Option[List[KillChainPhase]] = None,
                         revoked: Option[Boolean] = None,
                         labels: Option[List[String]] = None,
                         confidence: Option[Int] = None,
                         external_references: Option[List[ExternalReference]] = None,
                         lang: Option[String] = None,
                         object_marking_refs: Option[List[Identifier]] = None,
                         granular_markings: Option[List[GranularMarking]] = None,
                         created_by_ref: Option[Identifier] = None,
                         custom: Option[CustomProps] = None) extends SDO

object AttackPattern {
  val `type` = "attack-pattern"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(AttackPattern(name = "test"))

  val theReads = new Reads[AttackPattern] {
    def reads(js: JsValue): JsResult[AttackPattern] = {
      if ((js \ "type").asOpt[String].contains(AttackPattern.`type`)) {
        JsSuccess(new AttackPattern(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "description").asOpt[String],
          (js \ "kill_chain_phases").asOpt[List[KillChainPhase]],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading AttackPattern: $js")
      }
    }
  }

  val theWrites = new Writes[AttackPattern] {
    def writes(p: AttackPattern): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name))

      val theList = JsObject(List(
        p.description.map("description" -> JsString(_)),
        p.kill_chain_phases.map("kill_chain_phases" -> Json.toJson(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[AttackPattern] = Format(theReads, theWrites)
}

/**
  * Identities can represent actual individuals, organizations, or groups (e.g., ACME, Inc.) as well as
  * classes of individuals, organizations, or groups (e.g., the finance sector).
  */
case class Identity(`type`: String = Identity.`type`,
                    id: Identifier = Identifier(Identity.`type`),
                    created: Timestamp = Timestamp.now(),
                    modified: Timestamp = Timestamp.now(),
                    name: String,
                    identity_class: String,
                    sectors: Option[List[String]] = None,
                    contact_information: Option[String] = None,
                    description: Option[String] = None,
                    revoked: Option[Boolean] = None,
                    labels: Option[List[String]] = None,
                    confidence: Option[Int] = None,
                    external_references: Option[List[ExternalReference]] = None,
                    lang: Option[String] = None,
                    object_marking_refs: Option[List[Identifier]] = None,
                    granular_markings: Option[List[GranularMarking]] = None,
                    created_by_ref: Option[Identifier] = None,
                    //    location: Option[Location] = None,
                    custom: Option[CustomProps] = None) extends SDO

object Identity {
  val `type` = "identity"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Identity(name = "test", identity_class = "test"))

  val theReads = new Reads[Identity] {
    def reads(js: JsValue): JsResult[Identity] = {
      if ((js \ "type").asOpt[String].contains(Identity.`type`)) {
        JsSuccess(new Identity(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "identity_class").as[String],
          (js \ "sectors").asOpt[List[String]],
          (js \ "contact_information").asOpt[String],
          (js \ "description").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Identity: $js")
      }
    }
  }

  val theWrites = new Writes[Identity] {
    def writes(p: Identity): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name),
        "identity_class" -> JsString(p.name))

      val theList = JsObject(List(
        p.sectors.map("sectors" -> Json.toJson(_)),
        p.contact_information.map("contact_information" -> JsString(_)),
        p.description.map("description" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Identity] = Format(theReads, theWrites)
}

/**
  * A Campaign is a grouping of adversarial behaviors that describes a set of malicious activities or
  * attacks (sometimes called waves) that occur over a period of time against a specific set of targets.
  * Campaigns usually have well defined objectives and may be part of an Intrusion Set.
  */
case class Campaign(`type`: String = Campaign.`type`,
                    id: Identifier = Identifier(Campaign.`type`),
                    created: Timestamp = Timestamp.now(),
                    modified: Timestamp = Timestamp.now(),
                    name: String,
                    description: Option[String] = None,
                    aliases: Option[List[String]] = None,
                    first_seen: Option[Timestamp] = None,
                    last_seen: Option[Timestamp] = None,
                    objective: Option[String] = None,
                    revoked: Option[Boolean] = None,
                    labels: Option[List[String]] = None,
                    confidence: Option[Int] = None,
                    external_references: Option[List[ExternalReference]] = None,
                    lang: Option[String] = None,
                    object_marking_refs: Option[List[Identifier]] = None,
                    granular_markings: Option[List[GranularMarking]] = None,
                    created_by_ref: Option[Identifier] = None,
                    custom: Option[CustomProps] = None) extends SDO

object Campaign {
  val `type` = "campaign"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Campaign(name = "test"))

  val theReads = new Reads[Campaign] {
    def reads(js: JsValue): JsResult[Campaign] = {
      if ((js \ "type").asOpt[String].contains(Campaign.`type`)) {
        JsSuccess(new Campaign(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "description").asOpt[String],
          (js \ "aliases").asOpt[List[String]],
          (js \ "first_seen").asOpt[Timestamp],
          (js \ "last_seen").asOpt[Timestamp],
          (js \ "objective").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Campaign: $js")
      }
    }
  }

  val theWrites = new Writes[Campaign] {
    def writes(p: Campaign): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name))

      val theList = JsObject(List(
        p.description.map("description" -> JsString(_)),
        p.aliases.map("aliases" -> Json.toJson(_)),
        p.first_seen.map("first_seen" -> Json.toJson(_)),
        p.last_seen.map("last_seen" -> Json.toJson(_)),
        p.objective.map("objective" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Campaign] = Format(theReads, theWrites)
}

/**
  * A Course of Action is an action taken either to prevent an attack or to respond to an attack that is in progress.
  */
case class CourseOfAction(`type`: String = CourseOfAction.`type`,
                          id: Identifier = Identifier(CourseOfAction.`type`),
                          created: Timestamp = Timestamp.now(),
                          modified: Timestamp = Timestamp.now(),
                          name: String,
                          description: Option[String] = None,
                          revoked: Option[Boolean] = None,
                          labels: Option[List[String]] = None,
                          confidence: Option[Int] = None,
                          external_references: Option[List[ExternalReference]] = None,
                          lang: Option[String] = None,
                          object_marking_refs: Option[List[Identifier]] = None,
                          granular_markings: Option[List[GranularMarking]] = None,
                          created_by_ref: Option[Identifier] = None,
                          custom: Option[CustomProps] = None) extends SDO

object CourseOfAction {
  val `type` = "course-of-action"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(CourseOfAction(name = "test"))

  val theReads = new Reads[CourseOfAction] {
    def reads(js: JsValue): JsResult[CourseOfAction] = {
      if ((js \ "type").asOpt[String].contains(CourseOfAction.`type`)) {
        JsSuccess(new CourseOfAction(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "description").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading CourseOfAction: $js")
      }
    }
  }

  val theWrites = new Writes[CourseOfAction] {
    def writes(p: CourseOfAction): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name))

      val theList = JsObject(List(
        p.description.map("description" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[CourseOfAction] = Format(theReads, theWrites)
}

/**
  * Indicators contain a pattern that can be used to detect suspicious or malicious cyber activity.
  */
case class Indicator(`type`: String = Indicator.`type`,
                     id: Identifier = Identifier(Indicator.`type`),
                     created: Timestamp = Timestamp.now(),
                     modified: Timestamp = Timestamp.now(),
                     pattern: String,
                     valid_from: Timestamp,
                     name: Option[String] = None,
                     valid_until: Option[Timestamp] = None,
                     labels: Option[List[String]] = None, // todo ---> should not be optional
                     kill_chain_phases: Option[List[KillChainPhase]] = None,
                     description: Option[String] = None,
                     revoked: Option[Boolean] = None,
                     confidence: Option[Int] = None,
                     external_references: Option[List[ExternalReference]] = None,
                     lang: Option[String] = None,
                     object_marking_refs: Option[List[Identifier]] = None,
                     granular_markings: Option[List[GranularMarking]] = None,
                     created_by_ref: Option[Identifier] = None,
                     custom: Option[CustomProps] = None) extends SDO

object Indicator {
  val `type` = "indicator"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Indicator(pattern = "test", valid_from = Timestamp.now()))

  val theReads = new Reads[Indicator] {
    def reads(js: JsValue): JsResult[Indicator] = {
      if ((js \ "type").asOpt[String].contains(Indicator.`type`)) {
        JsSuccess(new Indicator(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "pattern").as[String],
          (js \ "valid_from").as[Timestamp],
          (js \ "name").asOpt[String],
          (js \ "valid_until").asOpt[Timestamp],
          (js \ "labels").asOpt[List[String]],
          (js \ "kill_chain_phases").asOpt[List[KillChainPhase]],
          (js \ "description").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Indicator: $js")
      }
    }
  }

  val theWrites = new Writes[Indicator] {
    def writes(p: Indicator): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "pattern" -> JsString(p.pattern),
        "valid_from" -> Json.toJson(p.valid_from))

      val theList = JsObject(List(
        p.name.map("name" -> JsString(_)),
        p.valid_until.map("valid_until" -> Json.toJson(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.kill_chain_phases.map("kill_chain_phases" -> Json.toJson(_)),
        p.description.map("description" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Indicator] = Format(theReads, theWrites)
}

/**
  * An Intrusion Set is a grouped set of adversarial behaviors and resources with common properties that is believed
  * to be orchestrated by a single organization.
  */
case class IntrusionSet(`type`: String = IntrusionSet.`type`,
                        id: Identifier = Identifier(IntrusionSet.`type`),
                        created: Timestamp = Timestamp.now(),
                        modified: Timestamp = Timestamp.now(),
                        name: String,
                        description: Option[String] = None,
                        aliases: Option[List[String]] = None,
                        first_seen: Option[Timestamp] = None,
                        last_seen: Option[Timestamp] = None,
                        goals: Option[List[String]] = None,
                        resource_level: Option[String] = None,
                        primary_motivation: Option[String] = None,
                        secondary_motivations: Option[List[String]] = None,
                        revoked: Option[Boolean] = None,
                        labels: Option[List[String]] = None,
                        confidence: Option[Int] = None,
                        external_references: Option[List[ExternalReference]] = None,
                        lang: Option[String] = None,
                        object_marking_refs: Option[List[Identifier]] = None,
                        granular_markings: Option[List[GranularMarking]] = None,
                        created_by_ref: Option[Identifier] = None,
                        //    locations: Option[List[Location]] = None,
                        custom: Option[CustomProps] = None) extends SDO

object IntrusionSet {
  val `type` = "intrusion-set"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(IntrusionSet(name = "test"))

  val theReads = new Reads[IntrusionSet] {
    def reads(js: JsValue): JsResult[IntrusionSet] = {
      if ((js \ "type").asOpt[String].contains(IntrusionSet.`type`)) {
        JsSuccess(new IntrusionSet(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "description").asOpt[String],
          (js \ "aliases").asOpt[List[String]],
          (js \ "first_seen").asOpt[Timestamp],
          (js \ "last_seen").asOpt[Timestamp],
          (js \ "goals").asOpt[List[String]],
          (js \ "resource_level").asOpt[String],
          (js \ "primary_motivation").asOpt[String],
          (js \ "secondary_motivations").asOpt[List[String]],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading IntrusionSet: $js")
      }
    }
  }

  val theWrites = new Writes[IntrusionSet] {
    def writes(p: IntrusionSet): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name))

      val theList = JsObject(List(
        p.description.map("description" -> JsString(_)),
        p.aliases.map("aliases" -> Json.toJson(_)),
        p.first_seen.map("first_seen" -> Json.toJson(_)),
        p.last_seen.map("last_seen" -> Json.toJson(_)),
        p.goals.map("goals" -> Json.toJson(_)),
        p.resource_level.map("resource_level" -> JsString(_)),
        p.primary_motivation.map("primary_motivation" -> JsString(_)),
        p.secondary_motivations.map("last_seen" -> Json.toJson(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[IntrusionSet] = Format(theReads, theWrites)
}

/**
  * Malware is a type of TTP that is also known as malicious code and malicious software,
  * and refers to a program that is inserted into a system, usually covertly,
  * with the intent of compromising the confidentiality, integrity, or availability of
  * the victim's data, applications, or operating system (OS) or of otherwise annoying or
  * disrupting the victim.
  */
case class Malware(`type`: String = Malware.`type`,
                   id: Identifier = Identifier(Malware.`type`),
                   created: Timestamp = Timestamp.now(),
                   modified: Timestamp = Timestamp.now(),
                   name: String,
                   description: Option[String] = None,
                   kill_chain_phases: Option[List[KillChainPhase]] = None,
                   revoked: Option[Boolean] = None,
                   labels: Option[List[String]] = None,
                   confidence: Option[Int] = None,
                   external_references: Option[List[ExternalReference]] = None,
                   lang: Option[String] = None,
                   object_marking_refs: Option[List[Identifier]] = None,
                   granular_markings: Option[List[GranularMarking]] = None,
                   created_by_ref: Option[Identifier] = None,
                   custom: Option[CustomProps] = None) extends SDO

object Malware {
  val `type` = "malware"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Malware(name = "test"))

  val theReads = new Reads[Malware] {
    def reads(js: JsValue): JsResult[Malware] = {
      if ((js \ "type").asOpt[String].contains(Malware.`type`)) {
        JsSuccess(new Malware(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "description").asOpt[String],
          (js \ "kill_chain_phases").asOpt[List[KillChainPhase]],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Malware: $js")
      }
    }
  }

  val theWrites = new Writes[Malware] {
    def writes(p: Malware): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name))

      val theList = JsObject(List(
        p.description.map("description" -> JsString(_)),
        p.kill_chain_phases.map("kill_chain_phases" -> Json.toJson(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Malware] = Format(theReads, theWrites)
}

/**
  * Observed Data conveys information that was observed on systems and networks using the Cyber Observable specification
  * defined in parts 3 and 4 of this specification.
  */
case class ObservedData(`type`: String = ObservedData.`type`,
                        id: Identifier = Identifier(ObservedData.`type`),
                        created: Timestamp = Timestamp.now(),
                        modified: Timestamp = Timestamp.now(),
                        first_observed: Timestamp,
                        last_observed: Timestamp,
                        number_observed: Int,
                        objects: Map[String, Observable],
                        description: Option[String] = None,
                        revoked: Option[Boolean] = None,
                        labels: Option[List[String]] = None,
                        confidence: Option[Int] = None,
                        external_references: Option[List[ExternalReference]] = None,
                        lang: Option[String] = None,
                        object_marking_refs: Option[List[Identifier]] = None,
                        granular_markings: Option[List[GranularMarking]] = None,
                        created_by_ref: Option[Identifier] = None,
                        custom: Option[CustomProps] = None) extends SDO

object ObservedData {
  val `type` = "observed-data"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(ObservedData(first_observed = Timestamp.now(), last_observed = Timestamp.now(),
    number_observed = 0,
    objects = Map[String, Observable]()))

  val theReads = new Reads[ObservedData] {
    def reads(js: JsValue): JsResult[ObservedData] = {
      if ((js \ "type").asOpt[String].contains(ObservedData.`type`)) {
        JsSuccess(new ObservedData(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "first_observed").as[Timestamp],
          (js \ "last_observed").as[Timestamp],
          (js \ "number_observed").as[Int],
          (js \ "objects").as[Map[String, Observable]],
          (js \ "description").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading ObservedData: $js")
      }
    }
  }

  val theWrites = new Writes[ObservedData] {
    def writes(p: ObservedData): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "first_observed" -> Json.toJson(p.first_observed),
        "last_observed" -> Json.toJson(p.last_observed),
        "number_observed" -> JsNumber(p.number_observed),
        "objects" -> Json.toJson(p.objects))

      val theList = JsObject(List(
        p.description.map("description" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[ObservedData] = Format(theReads, theWrites)
}

/**
  * Reports are collections of threat intelligence focused on one or more topics, such as a description of
  * a threat actor, malware, or attack technique, including context and related details.
  */
case class Report(`type`: String = Report.`type`,
                  id: Identifier = Identifier(Report.`type`),
                  created: Timestamp = Timestamp.now(),
                  modified: Timestamp = Timestamp.now(),
                  name: String,
                  published: Timestamp,
                  object_refs: Option[List[Identifier]] = None,
                  description: Option[String] = None,
                  revoked: Option[Boolean] = None,
                  labels: Option[List[String]] = None,
                  confidence: Option[Int] = None,
                  external_references: Option[List[ExternalReference]] = None,
                  lang: Option[String] = None,
                  object_marking_refs: Option[List[Identifier]] = None,
                  granular_markings: Option[List[GranularMarking]] = None,
                  created_by_ref: Option[Identifier] = None,
                  custom: Option[CustomProps] = None) extends SDO

object Report {
  val `type` = "report"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Report(name = "test", published = Timestamp.now()))

  val theReads = new Reads[Report] {
    def reads(js: JsValue): JsResult[Report] = {
      if ((js \ "type").asOpt[String].contains(Report.`type`)) {
        JsSuccess(new Report(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "published").as[Timestamp],
          (js \ "object_refs").asOpt[List[Identifier]],
          (js \ "description").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Report: $js")
      }
    }
  }

  val theWrites = new Writes[Report] {
    def writes(p: Report): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name),
        "published" -> Json.toJson(p.published))

      val theList = JsObject(List(
        p.object_refs.map("object_refs" -> Json.toJson(_)),
        p.description.map("description" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Report] = Format(theReads, theWrites)
}

/**
  * Threat Actors are actual individuals, groups,
  * or organizations believed to be operating with malicious intent.
  */
case class ThreatActor(`type`: String = ThreatActor.`type`,
                       id: Identifier = Identifier(ThreatActor.`type`),
                       created: Timestamp = Timestamp.now(),
                       modified: Timestamp = Timestamp.now(),
                       name: String,
                       labels: Option[List[String]] = None, // todo ---> should not be optional
                       description: Option[String] = None,
                       aliases: Option[List[String]] = None,
                       roles: Option[List[String]] = None,
                       goals: Option[List[String]] = None,
                       sophistication: Option[String] = None,
                       resource_level: Option[String] = None,
                       primary_motivation: Option[String] = None,
                       secondary_motivations: Option[List[String]] = None,
                       personal_motivations: Option[List[String]] = None,
                       revoked: Option[Boolean] = None,
                       confidence: Option[Int] = None,
                       external_references: Option[List[ExternalReference]] = None,
                       lang: Option[String] = None,
                       object_marking_refs: Option[List[Identifier]] = None,
                       granular_markings: Option[List[GranularMarking]] = None,
                       created_by_ref: Option[Identifier] = None,
                       custom: Option[CustomProps] = None) extends SDO

object ThreatActor {
  val `type` = "threat-actor"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(ThreatActor(name = "test"))

  val theReads = new Reads[ThreatActor] {
    def reads(js: JsValue): JsResult[ThreatActor] = {
      if ((js \ "type").asOpt[String].contains(ThreatActor.`type`)) {
        JsSuccess(new ThreatActor(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "labels").asOpt[List[String]],
          (js \ "description").asOpt[String],
          (js \ "aliases").asOpt[List[String]],
          (js \ "roles").asOpt[List[String]],
          (js \ "goals").asOpt[List[String]],
          (js \ "sophistication").asOpt[String],
          (js \ "resource_level").asOpt[String],
          (js \ "primary_motivation").asOpt[String],
          (js \ "secondary_motivations").asOpt[List[String]],
          (js \ "personal_motivations").asOpt[List[String]],
          (js \ "revoked").asOpt[Boolean],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading ThreatActor: $js")
      }
    }
  }

  val theWrites = new Writes[ThreatActor] {
    def writes(p: ThreatActor): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name))

      val theList = JsObject(List(
        p.labels.map("labels" -> Json.toJson(_)),
        p.description.map("description" -> JsString(_)),
        p.aliases.map("aliases" -> Json.toJson(_)),
        p.roles.map("roles" -> Json.toJson(_)),
        p.goals.map("goals" -> Json.toJson(_)),
        p.sophistication.map("sophistication" -> JsString(_)),
        p.resource_level.map("resource_level" -> JsString(_)),
        p.primary_motivation.map("primary_motivation" -> JsString(_)),
        p.secondary_motivations.map("secondary_motivations" -> Json.toJson(_)),
        p.personal_motivations.map("personal_motivations" -> Json.toJson(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[ThreatActor] = Format(theReads, theWrites)
}

/**
  * Tools are legitimate software that can be used by threat actors to perform attacks.
  */
case class Tool(`type`: String = Tool.`type`,
                id: Identifier = Identifier(Tool.`type`),
                created: Timestamp = Timestamp.now(),
                modified: Timestamp = Timestamp.now(),
                name: String,
                labels: Option[List[String]] = None, // todo ---> should not be optional
                description: Option[String] = None,
                kill_chain_phases: Option[List[KillChainPhase]] = None,
                tool_version: Option[String] = None,
                revoked: Option[Boolean] = None,
                confidence: Option[Int] = None,
                external_references: Option[List[ExternalReference]] = None,
                lang: Option[String] = None,
                object_marking_refs: Option[List[Identifier]] = None,
                granular_markings: Option[List[GranularMarking]] = None,
                created_by_ref: Option[Identifier] = None,
                custom: Option[CustomProps] = None) extends SDO

object Tool {
  val `type` = "tool"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Tool(name = "test"))

  val theReads = new Reads[Tool] {
    def reads(js: JsValue): JsResult[Tool] = {
      if ((js \ "type").asOpt[String].contains(Tool.`type`)) {
        JsSuccess(new Tool(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "labels").asOpt[List[String]],
          (js \ "description").asOpt[String],
          (js \ "kill_chain_phases").asOpt[List[KillChainPhase]],
          (js \ "tool_version").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Tool: $js")
      }
    }
  }

  val theWrites = new Writes[Tool] {
    def writes(p: Tool): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name))

      val theList = JsObject(List(
        p.tool_version.map("tool_version" -> JsString(_)),
        p.description.map("description" -> JsString(_)),
        p.kill_chain_phases.map("kill_chain_phases" -> Json.toJson(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Tool] = Format(theReads, theWrites)
}

/**
  * A Vulnerability is "a mistake in software that can be directly used by a hacker
  * to gain access to a system or network"
  */
case class Vulnerability(`type`: String = Vulnerability.`type`,
                         id: Identifier = Identifier(Vulnerability.`type`),
                         created: Timestamp = Timestamp.now(),
                         modified: Timestamp = Timestamp.now(),
                         name: String,
                         description: Option[String] = None,
                         revoked: Option[Boolean] = None,
                         labels: Option[List[String]] = None,
                         confidence: Option[Int] = None,
                         external_references: Option[List[ExternalReference]] = None,
                         lang: Option[String] = None,
                         object_marking_refs: Option[List[Identifier]] = None,
                         granular_markings: Option[List[GranularMarking]] = None,
                         created_by_ref: Option[Identifier] = None,
                         custom: Option[CustomProps] = None) extends SDO

object Vulnerability {
  val `type` = "vulnerability"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Vulnerability(name = "test"))

  val theReads = new Reads[Vulnerability] {
    def reads(js: JsValue): JsResult[Vulnerability] = {
      if ((js \ "type").asOpt[String].contains(Vulnerability.`type`)) {
        JsSuccess(new Vulnerability(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "name").as[String],
          (js \ "description").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Vulnerability: $js")
      }
    }
  }

  val theWrites = new Writes[Vulnerability] {
    def writes(p: Vulnerability): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "name" -> JsString(p.name))

      val theList = JsObject(List(
        p.description.map("description" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Vulnerability] = Format(theReads, theWrites)
}

//-----------------------------------------------------------------------
//------------------Relationship objects----------------------------------
//-----------------------------------------------------------------------

trait SRO extends StixObj {
  val created: Timestamp
  val modified: Timestamp
  val created_by_ref: Option[Identifier]
  val revoked: Option[Boolean]
  val labels: Option[List[String]]
  val confidence: Option[Int]
  val external_references: Option[List[ExternalReference]]
  val lang: Option[String]
  val object_marking_refs: Option[List[Identifier]]
  val granular_markings: Option[List[GranularMarking]]
}

/**
  * The Relationship object is used to link together two SDOs in order to describe how
  * they are related to each other. If SDOs are considered "nodes" or "vertices" in the graph,
  * the Relationship Objects (SROs) represent "edges".
  */
case class Relationship(`type`: String = Relationship.`type`,
                        id: Identifier = Identifier(Relationship.`type`),
                        created: Timestamp = Timestamp.now(),
                        modified: Timestamp = Timestamp.now(),
                        source_ref: Identifier,
                        relationship_type: String,
                        target_ref: Identifier,
                        description: Option[String] = None,
                        revoked: Option[Boolean] = None,
                        labels: Option[List[String]] = None,
                        confidence: Option[Int] = None,
                        external_references: Option[List[ExternalReference]] = None,
                        lang: Option[String] = None,
                        object_marking_refs: Option[List[Identifier]] = None,
                        granular_markings: Option[List[GranularMarking]] = None,
                        created_by_ref: Option[Identifier] = None,
                        custom: Option[CustomProps] = None) extends SRO {

  def this(source_ref: Identifier, relationship_type: String, target_ref: Identifier) =
    this(Relationship.`type`, Identifier(Relationship.`type`), Timestamp.now(), Timestamp.now(),
      source_ref, relationship_type, target_ref)
}

object Relationship {
  val `type` = "relationship"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Relationship(source_ref = Identifier(Relationship.`type`), relationship_type = "test", target_ref = Identifier(Relationship.`type`)))

  val theReads = new Reads[Relationship] {
    def reads(js: JsValue): JsResult[Relationship] = {
      if ((js \ "type").asOpt[String].contains(Relationship.`type`)) {
        JsSuccess(new Relationship(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "source_ref").as[Identifier],
          (js \ "relationship_type").as[String],
          (js \ "target_ref").as[Identifier],
          (js \ "description").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Relationship: $js")
      }
    }
  }

  val theWrites = new Writes[Relationship] {
    def writes(p: Relationship): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "source_ref" -> Json.toJson(p.source_ref),
        "relationship_type" -> JsString(p.relationship_type),
        "target_ref" -> Json.toJson(p.target_ref))

      val theList = JsObject(List(
        p.description.map("description" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Relationship] = Format(theReads, theWrites)
}

/**
  * A Sighting denotes the belief that something in CTI (e.g., an indicator, malware, tool, threat actor, etc.) was seen.
  */
case class Sighting(`type`: String = Sighting.`type`,
                    id: Identifier = Identifier(Sighting.`type`),
                    created: Timestamp = Timestamp.now(),
                    modified: Timestamp = Timestamp.now(),
                    sighting_of_ref: Identifier,
                    first_seen: Option[Timestamp] = None,
                    last_seen: Option[Timestamp] = None,
                    count: Option[Int] = None,
                    observed_data_refs: Option[List[Identifier]] = None,
                    where_sighted_refs: Option[List[Identifier]] = None,
                    summary: Option[Boolean] = None,
                    description: Option[String] = None,
                    revoked: Option[Boolean] = None,
                    labels: Option[List[String]] = None,
                    confidence: Option[Int] = None,
                    external_references: Option[List[ExternalReference]] = None,
                    lang: Option[String] = None,
                    object_marking_refs: Option[List[Identifier]] = None,
                    granular_markings: Option[List[GranularMarking]] = None,
                    created_by_ref: Option[Identifier] = None,
                    custom: Option[CustomProps] = None) extends SRO

object Sighting {
  val `type` = "sighting"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(Sighting(sighting_of_ref = Identifier(Sighting.`type`)))

  val theReads = new Reads[Sighting] {
    def reads(js: JsValue): JsResult[Sighting] = {
      if ((js \ "type").asOpt[String].contains(Sighting.`type`)) {
        JsSuccess(new Sighting(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "sighting_of_ref").as[Identifier],
          (js \ "first_seen").asOpt[Timestamp],
          (js \ "last_seen").asOpt[Timestamp],
          (js \ "count").asOpt[Int],
          (js \ "observed_data_refs").asOpt[List[Identifier]],
          (js \ "where_sighted_refs").asOpt[List[Identifier]],
          (js \ "summary").asOpt[Boolean],
          (js \ "description").asOpt[String],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "confidence").asOpt[Int],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "lang").asOpt[String],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          (js \ "created_by_ref").asOpt[Identifier],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading Sighting: $js")
      }
    }
  }

  val theWrites = new Writes[Sighting] {
    def writes(p: Sighting): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "sighting_of_ref" -> Json.toJson(p.sighting_of_ref))

      val theList = JsObject(List(
        p.first_seen.map("first_seen" -> Json.toJson(_)),
        p.last_seen.map("last_seen" -> Json.toJson(_)),
        p.count.map("count" -> JsNumber(_)),
        p.observed_data_refs.map("observed_data_refs" -> Json.toJson(_)),
        p.where_sighted_refs.map("where_sighted_refs" -> Json.toJson(_)),
        p.summary.map("summary" -> JsBoolean(_)),
        p.description.map("description" -> JsString(_)),
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.confidence.map("confidence" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.lang.map("lang" -> JsString(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[Sighting] = Format(theReads, theWrites)
}

//-----------------------------------------------------------------------
//------------------Language content-------------------------------------
//-----------------------------------------------------------------------

/**
  * The language-content object represents text content for STIX Objects represented in other languages.
  */
case class LanguageContent(`type`: String = LanguageContent.`type`,
                           id: Identifier = Identifier(LanguageContent.`type`),
                           created: Timestamp = Timestamp.now(),
                           modified: Timestamp = Timestamp.now(),
                           object_modified: Timestamp = Timestamp.now(),
                           object_ref: Identifier,
                           contents: Map[String, Map[String, String]], // todo <-- RFC5646_LANGUAGE_TAG and List and object
                           created_by_ref: Option[Identifier] = None,
                           revoked: Option[Boolean] = None,
                           labels: Option[List[String]] = None,
                           external_references: Option[List[ExternalReference]] = None,
                           object_marking_refs: Option[List[Identifier]] = None,
                           granular_markings: Option[List[GranularMarking]] = None,
                           custom: Option[CustomProps] = None) extends StixObj

object LanguageContent {
  val `type` = "language-content"

  // get the names of all the fields but not the custom field.
  private val omitList = getOmitList(LanguageContent(object_ref = Identifier(LanguageContent.`type`), contents = Map[String, Map[String, String]]()))

  val theReads = new Reads[LanguageContent] {
    def reads(js: JsValue): JsResult[LanguageContent] = {
      if ((js \ "type").asOpt[String].contains(LanguageContent.`type`)) {
        JsSuccess(new LanguageContent(
          (js \ "type").as[String],
          (js \ "id").as[Identifier],
          (js \ "created").as[Timestamp],
          (js \ "modified").as[Timestamp],
          (js \ "object_modified").as[Timestamp],
          (js \ "object_ref").as[Identifier],
          (js \ "contents").as[Map[String, Map[String, String]]],
          (js \ "created_by_ref").asOpt[Identifier],
          (js \ "revoked").asOpt[Boolean],
          (js \ "labels").asOpt[List[String]],
          (js \ "external_references").asOpt[List[ExternalReference]],
          (js \ "object_marking_refs").asOpt[List[Identifier]],
          (js \ "granular_markings").asOpt[List[GranularMarking]],
          CustomProps.readAttributes(js, omitList)))
      }
      else {
        JsError(s"Error reading LanguageContent: $js")
      }
    }
  }

  val theWrites = new Writes[LanguageContent] {
    def writes(p: LanguageContent): JsValue = {
      val baseList = Json.obj(
        "type" -> JsString(p.`type`),
        "id" -> Json.toJson(p.id),
        "created" -> Json.toJson(p.created),
        "modified" -> Json.toJson(p.modified),
        "object_modified" -> Json.toJson(p.object_modified),
        "object_ref" -> Json.toJson(p.object_ref),
        "contents" -> Json.toJson(p.contents))

      val theList = JsObject(List(
        p.revoked.map("revoked" -> JsBoolean(_)),
        p.labels.map("labels" -> Json.toJson(_)),
        p.external_references.map("external_references" -> Json.toJson(_)),
        p.object_marking_refs.map("object_marking_refs" -> Json.toJson(_)),
        p.granular_markings.map("granular_markings" -> Json.toJson(_)),
        p.created_by_ref.map("created_by_ref" -> Json.toJson(_))
      ).flatten)

      p.custom match {
        case Some(cust) => baseList ++ theList ++ asJsObject(cust)
        case None => baseList ++ theList
      }
    }
  }

  implicit val fmt: Format[LanguageContent] = Format(theReads, theWrites)
}

//-----------------------------------------------------------------------
//------------------STIX and Bundle object-------------------------------
//-----------------------------------------------------------------------

object StixObj {

  val theReads = new Reads[StixObj] {
    def reads(js: JsValue): JsResult[StixObj] = {
      (js \ "type").asOpt[String].map({
        case AttackPattern.`type` => AttackPattern.fmt.reads(js)
        case Identity.`type` => Identity.fmt.reads(js)
        case Campaign.`type` => Campaign.fmt.reads(js)
        case CourseOfAction.`type` => CourseOfAction.fmt.reads(js)
        case Indicator.`type` => Indicator.fmt.reads(js)
        case IntrusionSet.`type` => IntrusionSet.fmt.reads(js)
        case Malware.`type` => Malware.fmt.reads(js)
        case ObservedData.`type` => ObservedData.fmt.reads(js)
        case Report.`type` => Report.fmt.reads(js)
        case ThreatActor.`type` => ThreatActor.fmt.reads(js)
        case Tool.`type` => Tool.fmt.reads(js)
        case Vulnerability.`type` => Vulnerability.fmt.reads(js)
        case Relationship.`type` => Relationship.fmt.reads(js)
        case Sighting.`type` => Sighting.fmt.reads(js)
        case MarkingDefinition.`type` => MarkingDefinition.fmt.reads(js)
        case LanguageContent.`type` => LanguageContent.fmt.reads(js)
      }).getOrElse(JsError("Error reading StixObj"))
    }
  }

  val theWrites = new Writes[StixObj] {
    def writes(obj: StixObj) = {
      obj match {
        case stix: AttackPattern => AttackPattern.fmt.writes(stix)
        case stix: Identity => Identity.fmt.writes(stix)
        case stix: Campaign => Campaign.fmt.writes(stix)
        case stix: CourseOfAction => CourseOfAction.fmt.writes(stix)
        case stix: Indicator => Indicator.fmt.writes(stix)
        case stix: IntrusionSet => IntrusionSet.fmt.writes(stix)
        case stix: Malware => Malware.fmt.writes(stix)
        case stix: ObservedData => ObservedData.fmt.writes(stix)
        case stix: Report => Report.fmt.writes(stix)
        case stix: ThreatActor => ThreatActor.fmt.writes(stix)
        case stix: Tool => Tool.fmt.writes(stix)
        case stix: Vulnerability => Vulnerability.fmt.writes(stix)
        case stix: Relationship => Relationship.fmt.writes(stix)
        case stix: Sighting => Sighting.fmt.writes(stix)
        case stix: MarkingDefinition => MarkingDefinition.fmt.writes(stix)
        case stix: LanguageContent => LanguageContent.fmt.writes(stix)
        case _ => JsNull
      }
    }
  }

  implicit val fmt: Format[StixObj] = Format(theReads, theWrites)
}

/**
  * A Bundle is a collection of arbitrary STIX Objects and Marking Definitions grouped together in a single container.
  *
  * @param id      An identifier for this Bundle.
  * @param objects Specifies a set of one or more STIX Objects.
  */
case class Bundle(`type`: String = Bundle.`type`,
                  id: Identifier = Identifier(Bundle.`type`),
                  spec_version: String = Bundle.spec_version,
                  objects: List[StixObj]) {

  def this(objects: List[StixObj]) = this(Bundle.`type`, Identifier(Bundle.`type`), Bundle.spec_version, objects)

  def this(objects: StixObj*) = this(objects.toList)

}

object Bundle {
  val `type` = "bundle"
  val spec_version = "2.1"

  def apply(objects: List[StixObj]) = new Bundle(objects)

  def apply(objects: StixObj*) = new Bundle(objects.toList)

  implicit val fmt = Json.format[Bundle]
}


