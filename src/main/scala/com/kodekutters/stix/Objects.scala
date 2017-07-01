package com.kodekutters.stix

import org.threeten.bp._

import java.util.UUID
import play.api.libs.json._
import play.api.libs.functional.syntax._

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

/**
  * a general STIX object representing the SDOs, SROs, LanguageContent and MarkingDefinition
  */
trait StixObj {
  val `type`: String
  val id: Identifier
  val x_custom: Option[JsObject]
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
                             x_custom: Option[JsObject] = None) extends StixObj

object MarkingDefinition {
  val `type` = "marking-definition"
  implicit val fmt = Json.format[MarkingDefinition]
}

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
                         x_custom: Option[JsObject] = None) extends SDO

object AttackPattern {
  val `type` = "attack-pattern"
  implicit val fmt = Json.format[AttackPattern]
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
                    x_custom: Option[JsObject] = None) extends SDO

object Identity {
  val `type` = "identity"
  implicit val fmt = Json.format[Identity]
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
                    x_custom: Option[JsObject] = None) extends SDO

object Campaign {
  val `type` = "campaign"
  implicit val fmt = Json.format[Campaign]
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
                          x_custom: Option[JsObject] = None) extends SDO

object CourseOfAction {
  val `type` = "course-of-action"
  implicit val fmt = Json.format[CourseOfAction]
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
                     x_custom: Option[JsObject] = None) extends SDO

object Indicator {
  val `type` = "indicator"
  implicit val fmt = Json.format[Indicator]
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
                        x_custom: Option[JsObject] = None) extends SDO

object IntrusionSet {
  val `type` = "intrusion-set"
  implicit val fmt = Json.format[IntrusionSet]
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
                   kill_chain_phases: Option[List[KillChainPhase]],
                   revoked: Option[Boolean] = None,
                   labels: Option[List[String]] = None,
                   confidence: Option[Int] = None,
                   external_references: Option[List[ExternalReference]] = None,
                   lang: Option[String] = None,
                   object_marking_refs: Option[List[Identifier]] = None,
                   granular_markings: Option[List[GranularMarking]] = None,
                   created_by_ref: Option[Identifier] = None,
                   x_custom: Option[JsObject] = None) extends SDO

object Malware {
  val `type` = "malware"
  implicit val fmt = Json.format[Malware]
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
                        x_custom: Option[JsObject] = None) extends SDO

object ObservedData {
  val `type` = "observed-data"
  implicit val fmt = Json.format[ObservedData]
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
                  x_custom: Option[JsObject] = None) extends SDO

object Report {
  val `type` = "report"
  implicit val fmt = Json.format[Report]
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
                       x_custom: Option[JsObject] = None) extends SDO

object ThreatActor {
  val `type` = "threat-actor"

  // all this bloat because cannot deal with more than 22 fields

  val part1: OFormat[(String, Identifier, Timestamp, Timestamp, String, Option[List[String]], Option[String],
    Option[List[String]], Option[List[String]], Option[List[String]], Option[String])] =
    ((__ \ "type").format[String] ~ (__ \ "id").format[Identifier] ~ (__ \ "created").format[Timestamp] ~
      (__ \ "modified").format[Timestamp] ~ (__ \ "name").format[String] ~
      (__ \ "labels").formatNullable[List[String]] ~
      (__ \ "description").formatNullable[String] ~ (__ \ "aliases").formatNullable[List[String]] ~
      (__ \ "roles").formatNullable[List[String]] ~ (__ \ "goals").formatNullable[List[String]] ~
      (__ \ "sophistication").formatNullable[String]).tupled

  val part2: OFormat[(Option[String], Option[String], Option[List[String]], Option[List[String]], Option[Boolean],
    Option[Int], Option[List[ExternalReference]],
    Option[String], Option[List[Identifier]], Option[List[GranularMarking]], Option[Identifier], Option[JsObject])] =
    ((__ \ "resource_level").formatNullable[String] ~ (__ \ "primary_motivation").formatNullable[String] ~
      (__ \ "secondary_motivations").formatNullable[List[String]] ~ (__ \ "personal_motivations").formatNullable[List[String]] ~
      (__ \ "revoked").formatNullable[Boolean] ~ (__ \ "confidence").formatNullable[Int] ~
      (__ \ "external_references").formatNullable[List[ExternalReference]] ~ (__ \ "lang").formatNullable[String] ~
      (__ \ "object_marking_refs").formatNullable[List[Identifier]] ~ (__ \ "granular_markings").formatNullable[List[GranularMarking]] ~
      (__ \ "created_by_ref").formatNullable[Identifier] ~ (__ \ "x_custom").formatNullable[JsObject]).tupled

  implicit val fmt: Format[ThreatActor] = (part1 ~ part2) ({
    case ((`type`, id, created, modified, name, labels, description, aliases, roles, goals, sophistication),
    (resource_level, primary_motivation, secondary_motivations, personal_motivations, revoked,
    confidence, external_references, lang, object_marking_refs, granular_markings, created_by_ref, x_custom)) =>
      new ThreatActor(`type`, id, created, modified, name, labels, description, aliases, roles, goals, sophistication,
        resource_level, primary_motivation, secondary_motivations, personal_motivations, revoked,
        confidence, external_references, lang, object_marking_refs, granular_markings, created_by_ref, x_custom)
  }, (t: ThreatActor) => ((t.`type`, t.id, t.created, t.modified, t.name, t.labels, t.description, t.aliases,
    t.roles, t.goals, t.sophistication), (t.resource_level, t.primary_motivation, t.secondary_motivations, t.personal_motivations, t.revoked,
    t.confidence, t.external_references, t.lang, t.object_marking_refs, t.granular_markings, t.created_by_ref, t.x_custom)))

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
                x_custom: Option[JsObject] = None) extends SDO

object Tool {
  val `type` = "tool"
  implicit val fmt = Json.format[Tool]
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
                         x_custom: Option[JsObject] = None) extends SDO

object Vulnerability {
  val `type` = "vulnerability"
  implicit val fmt = Json.format[Vulnerability]
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
                        x_custom: Option[JsObject] = None) extends SRO {

  def this(source_ref: Identifier, relationship_type: String, target_ref: Identifier) =
    this(Relationship.`type`, Identifier(Relationship.`type`), Timestamp.now(), Timestamp.now(),
      source_ref, relationship_type, target_ref)
}

object Relationship {
  val `type` = "relationship"
  implicit val fmt = Json.format[Relationship]
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
                    x_custom: Option[JsObject] = None) extends SRO

object Sighting {
  val `type` = "sighting"
  implicit val fmt = Json.format[Sighting]
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
                           x_custom: Option[JsObject] = None) extends StixObj

object LanguageContent {
  val `type` = "language-content"
  implicit val fmt = Json.format[LanguageContent]
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


