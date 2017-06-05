package com.kodekutters

import com.kodekutters.stix._
import io.circe.generic.auto._
import io.circe.syntax._
import StixImplicits._
import io.circe.{Json, Printer}
import io.circe.parser.parse

/**
  * a simple example
  */
object Example2 {
  def main(args: Array[String]): Unit = {
    test()
  }

  def test() = {
    // to remove the output of "null" for empty fields
    implicit val myPrinter = Printer.spaces2.copy(dropNullKeys = true)

    // create a sdo
    val attackPattern = new AttackPattern(
      name = "Spear Phishing",
      kill_chain_phases = List(KillChainPhase("Kill", "Bill")),
      external_references = List(ExternalReference("a-source-name")),
      object_marking_refs = List(Identifier(Campaign.`type`)))

    println("\n----> attackPattern: " + attackPattern)
    // convert to json
    println("\n----> attackPattern.asJson: " + myPrinter.pretty(attackPattern.asJson))
    // add to a bundle
    val bundle = Bundle(attackPattern)
    println("\n----> bundle: " + bundle)
    println("\n----> bundle.asJson: " + myPrinter.pretty(bundle.asJson))
    //
    // starting with a string
    val theString =
    """{"type": "attack-pattern", "name" : "Spear Phishing reloaded",
          "id" : "attack-pattern--0fe33f18-9717-4329-9179-429d7304ef73",
          "created": "2017-05-11T07:13:18.448Z",
          "modified": "2017-05-11T07:13:18.448Z",
          "kill_chain_phases": [
            {
             "kill_chain_name": "mandiant-attack-lifecycle-model",
             "phase_name": "establish-foothold"
           }
          ]
         }""".stripMargin

    // convert to json
    val theJson: Json = parse(theString).getOrElse(Json.Null)
    println("\n---> theJson: " + theJson)
    // convert to a (option) Stix object
    val attackOpt = theJson.as[StixObj].toOption
    println("\n-----> attackOpt: " + attackOpt)
  }
}
