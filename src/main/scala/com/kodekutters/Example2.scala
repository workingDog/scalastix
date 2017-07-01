package com.kodekutters

import com.kodekutters.stix._

import StixImplicits._
import play.api.libs.json.Json


/**
  * a simple example
  */
object Example2 {
  def main(args: Array[String]): Unit = {
    test()
  }

  def test() = {

    println("---> in Example2 test")

    // create a sdo
    val attackPattern = new AttackPattern(
      name = "Spear Phishing",
      kill_chain_phases = List(KillChainPhase("Kill", "Bill")),
      external_references = List(ExternalReference("a-source-name")),
      object_marking_refs = List(Identifier(Campaign.`type`)))

    println("\n----> attackPattern: " + attackPattern)
    // convert to json
    println(Json.prettyPrint(Json.toJson(attackPattern)))
    // add to a bundle
    val bundle = Bundle(attackPattern)
    println("\n----> bundle: " + bundle)
    println("\n----> bundle to json: " + Json.prettyPrint(Json.toJson(bundle)))
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

    // parse to json
    val theJson = Json.parse(theString)
    println("\n---> theJson: " + Json.prettyPrint(theJson))
    // convert to a (option) Stix object
    val attackOpt = Json.fromJson[StixObj](theJson).asOpt
    println("\n---> attackOpt: " + attackOpt)
  }
}
