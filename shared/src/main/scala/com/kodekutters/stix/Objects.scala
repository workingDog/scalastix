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
}

object GranularMarking {
  val `type` = "granular-marking"

  implicit val fmt = Json.format[GranularMarking]
}

/**
  * External references are used to describe pointers to information represented outside of STIX.
  */
case class ExternalReference(source_name: String, description: Option[String] = None,
                             external_id: Option[String] = None,
                             url: Option[String] = None,
                             hashes: Option[Map[String, String]] = None) {
  val `type` = ExternalReference.`type`
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

  val theWrites = new Writes[CustomProps] {
    def writes(custom: CustomProps): JsObject = JsObject(custom.nodes)
  }

  val theReads = new Reads[CustomProps] {
    def reads(json: JsValue): JsResult[CustomProps] = {
      json match {
        case js: JsObject =>
          val fList = js.fields.filter(p => p._1.startsWith("x_"))
          JsSuccess(new CustomProps(fList.toMap))

        case x => JsError(s"Error could not read custom: $x")
      }
    }
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
  val custom: Option[CustomProps] // the custom properties as a map of property names and JsValues
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

  implicit val fmt: Format[MarkingDefinition] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "definition_type").format[String] and
      (__ \ "definition").format[MarkingObject] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (MarkingDefinition.apply, unlift(MarkingDefinition.unapply))

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
  * common properties of all SDO
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

  implicit val fmt: Format[AttackPattern] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "description").formatNullable[String] and
      (__ \ "kill_chain_phases").formatNullable[List[KillChainPhase]] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (AttackPattern.apply, unlift(AttackPattern.unapply))

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

  implicit val fmt: Format[Identity] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "identity_class").format[String] and
      (__ \ "sectors").formatNullable[List[String]] and
      (__ \ "contact_information").formatNullable[String] and
      (__ \ "description").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Identity.apply, unlift(Identity.unapply))

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

  implicit val fmt: Format[Campaign] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "description").formatNullable[String] and
      (__ \ "aliases").formatNullable[List[String]] and
      (__ \ "first_seen").formatNullable[Timestamp] and
      (__ \ "last_seen").formatNullable[Timestamp] and
      (__ \ "objective").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Campaign.apply, unlift(Campaign.unapply))

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

  implicit val fmt: Format[CourseOfAction] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "description").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (CourseOfAction.apply, unlift(CourseOfAction.unapply))

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

  implicit val fmt: Format[Indicator] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "pattern").format[String] and
      (__ \ "valid_from").format[Timestamp] and
      (__ \ "name").formatNullable[String] and
      (__ \ "valid_until").formatNullable[Timestamp] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "kill_chain_phases").formatNullable[List[KillChainPhase]] and
      (__ \ "description").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Indicator.apply, unlift(Indicator.unapply))

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

  implicit val fmt: Format[IntrusionSet] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "description").formatNullable[String] and
      (__ \ "aliases").formatNullable[List[String]] and
      (__ \ "first_seen").formatNullable[Timestamp] and
      (__ \ "last_seen").formatNullable[Timestamp] and
      (__ \ "goals").formatNullable[List[String]] and
      (__ \ "resource_level").formatNullable[String] and
      (__ \ "primary_motivation").formatNullable[String] and
      (__ \ "secondary_motivations").formatNullable[List[String]] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (IntrusionSet.apply, unlift(IntrusionSet.unapply))

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

  implicit val fmt: Format[Malware] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "description").formatNullable[String] and
      (__ \ "kill_chain_phases").formatNullable[List[KillChainPhase]] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Malware.apply, unlift(Malware.unapply))

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

  implicit val fmt: Format[ObservedData] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "first_observed").format[Timestamp] and
      (__ \ "last_observed").format[Timestamp] and
      (__ \ "number_observed").format[Int] and
      (__ \ "objects").format[Map[String, Observable]] and
      (__ \ "description").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (ObservedData.apply, unlift(ObservedData.unapply))

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

  implicit val fmt: Format[Report] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "published").format[Timestamp] and
      (__ \ "object_refs").formatNullable[List[Identifier]] and
      (__ \ "description").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Report.apply, unlift(Report.unapply))

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
          CustomProps.theReads.reads(js).asOpt))
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

  // still cannot cope with > 22 attributes

  //  implicit val fmt: Format[ThreatActor] = (
  //    (__ \ "type").format[String] and
  //      (__ \ "id").format[Identifier] and
  //      (__ \ "created").format[Timestamp] and
  //      (__ \ "modified").format[Timestamp] and
  //      (__ \ "name").format[String] and
  //      (__ \ "labels").formatNullable[List[String]] and
  //      (__ \ "description").formatNullable[String] and
  //      (__ \ "aliases").formatNullable[List[String]] and
  //      (__ \ "roles").formatNullable[List[String]] and
  //      (__ \ "goals").formatNullable[List[String]] and
  //      (__ \ "sophistication").formatNullable[String] and
  //      (__ \ "resource_level").formatNullable[String] and
  //      (__ \ "primary_motivation").formatNullable[String] and
  //      (__ \ "secondary_motivations").formatNullable[List[String]] and
  //      (__ \ "personal_motivations").formatNullable[List[String]] and
  //      (__ \ "revoked").formatNullable[Boolean] and
  //      (__ \ "confidence").formatNullable[Int] and
  //      (__ \ "external_references").formatNullable[List[ExternalReference]] and
  //      (__ \ "lang").formatNullable[String] and
  //      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
  //      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
  //      (__ \ "created_by_ref").formatNullable[Identifier] and
  //      JsPath.formatNullable[CustomProps]
  //    ) (ThreatActor.apply, unlift(ThreatActor.unapply))

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

  implicit val fmt: Format[Tool] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "description").formatNullable[String] and
      (__ \ "kill_chain_phases").formatNullable[List[KillChainPhase]] and
      (__ \ "tool_version").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Tool.apply, unlift(Tool.unapply))

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

  implicit val fmt: Format[Vulnerability] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "name").format[String] and
      (__ \ "description").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Vulnerability.apply, unlift(Vulnerability.unapply))

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

  implicit val fmt: Format[Relationship] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "source_ref").format[Identifier] and
      (__ \ "relationship_type").format[String] and
      (__ \ "target_ref").format[Identifier] and
      (__ \ "description").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Relationship.apply, unlift(Relationship.unapply))

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

  implicit val fmt: Format[Sighting] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "sighting_of_ref").format[Identifier] and
      (__ \ "first_seen").formatNullable[Timestamp] and
      (__ \ "last_seen").formatNullable[Timestamp] and
      (__ \ "count").formatNullable[Int] and
      (__ \ "observed_data_refs").formatNullable[List[Identifier]] and
      (__ \ "where_sighted_refs").formatNullable[List[Identifier]] and
      (__ \ "summary").formatNullable[Boolean] and
      (__ \ "description").formatNullable[String] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "confidence").formatNullable[Int] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "lang").formatNullable[String] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      JsPath.formatNullable[CustomProps]
    ) (Sighting.apply, unlift(Sighting.unapply))

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

  implicit val fmt: Format[LanguageContent] = (
    (__ \ "type").format[String] and
      (__ \ "id").format[Identifier] and
      (__ \ "created").format[Timestamp] and
      (__ \ "modified").format[Timestamp] and
      (__ \ "object_modified").format[Timestamp] and
      (__ \ "object_ref").format[Identifier] and
      (__ \ "contents").format[Map[String, Map[String, String]]] and
      (__ \ "created_by_ref").formatNullable[Identifier] and
      (__ \ "revoked").formatNullable[Boolean] and
      (__ \ "labels").formatNullable[List[String]] and
      (__ \ "external_references").formatNullable[List[ExternalReference]] and
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] and
      (__ \ "granular_markings").formatNullable[List[GranularMarking]] and
      JsPath.formatNullable[CustomProps]
    ) (LanguageContent.apply, unlift(LanguageContent.unapply))

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


