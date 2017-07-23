package com.kodekutters

import com.kodekutters.stix._
import StixImplicits._
import play.api.libs.json.{JsString, Json}


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
      object_marking_refs = List(Identifier(Campaign.`type`)),
      custom = CustomProps(Map("x_test" -> JsString("test1"))))

    println("\n----> attackPattern: " + attackPattern)
    // convert to json
    println(Json.prettyPrint(Json.toJson(attackPattern)))
    // add to a bundle
    val bundle = Bundle(attackPattern)
    println("\n----> bundle: " + bundle)
    println("\n----> bundle to json: " + Json.prettyPrint(Json.toJson(bundle)))

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
          ],
                "external_references": [
         {
           "source_name": "capec",
           "external_id": "CAPEC-148",
           "hashes": {
             "MD5": "66e2ea40dc71d5ba701574ea215a81f1",
             "MD1": "66e2ea4sssss215a81f1",
             "ZZZ": "xcxcvfgbfgb tyh tyh"
           }
         }
       ],
       "x_a": "some text",
       "x_b": 12,
       "x_c": false,
       "x_d": 45.67,
       "x_array_a": [1,2,3,4,5,6],
       "x_array_b": ["1", "2", "3", "4"],
       "x_obj": {"name": "Jack", "age": 27},
       "dog": "rover"
      }""".stripMargin

    // parse to json
    val theJson = Json.parse(theString)
    println("\n---> theJson: " + Json.prettyPrint(theJson))
    // convert to a (option) Stix object
    val attackOpt = Json.fromJson[StixObj](theJson).asOpt
    println("\n---> attackOpt: " + attackOpt)

  }

}
