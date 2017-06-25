package com.kodekutters

import scala.io.Source
import com.kodekutters.stix._
import play.api.libs.json.Json


/**
  * a simple example
  */
object Example1 {
  def main(args: Array[String]): Unit = {

    // read a STIX bundle from a file
    val jsondoc = Source.fromFile("./stixfiles/test2.json").mkString
    // create a bundle object from it
    Json.fromJson[Bundle](Json.parse(jsondoc)).asOpt match {
      case None => println("\n-----> invalid JSON ")
      case Some(bundle) =>
        println("\n-----> bundle: " + bundle)
        // back to json
        println(Json.prettyPrint(Json.toJson(bundle)))
        // print all individual sdo
        bundle.objects.foreach(sdo => println("sdo: " + sdo))
        // print all individual sdo as json
        bundle.objects.foreach(sdo => println("sdo to json: " + Json.prettyPrint(Json.toJson(sdo))))
        //  get all attackPattern
        val allPat = bundle.objects.filter(_.`type` == AttackPattern.`type`)
        allPat.foreach(x => println("\n-----> allPat: " + x))
        allPat.foreach(x => println(Json.prettyPrint(Json.toJson(x))))
    }
  }
}
