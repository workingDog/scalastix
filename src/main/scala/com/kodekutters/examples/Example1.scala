package com.kodekutters.examples

import scala.io.Source
import com.kodekutters.stix._
import play.api.libs.json.Json


/**
  * a simple example
  */
object Example1 {
  def main(args: Array[String]): Unit = {

    // read a STIX-2 bundle from a file
    val jsondoc = Source.fromFile("testfull.json").mkString
    // create a bundle object from it
    Json.fromJson[Bundle](Json.parse(jsondoc)).asOpt match {
      case Some(bundle) =>
        println("---> count: " + bundle.objects.length)
        // print all individual sdo
  //      bundle.objects.foreach(sdo => println("sdo: " + sdo))
        // get all attack pattern
  //      val allAttacks = bundle.objects.filter(_.`type` == AttackPattern.`type`)
  //      allAttacks.foreach(x => println("attack: " + x))
        // convert them to json and print
  //      allAttacks.foreach(x => println(Json.prettyPrint(Json.toJson(x))))

      case None => println("invalid JSON")
    }

  }
}
