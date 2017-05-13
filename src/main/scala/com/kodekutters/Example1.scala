package com.kodekutters

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.io.Source
import com.kodekutters.stix._

/**
  * a simple example
  */
object Example1 {
  def main(args: Array[String]): Unit = {
    // read a STIX bundle from a file
    val jsondoc = Source.fromFile("./stixfiles/test1.json").mkString
    // create a bundle object from it
    decode[Bundle](jsondoc) match {
      case Left(failure) => println("\n-----> invalid JSON ")
      case Right(bundle) =>
        print("\n-----> bundle: " + bundle)
        // back to json
        println("\n-----> bundle.asJson: " + bundle.asJson)
        // print all individual sdo
        bundle.objects.foreach(sdo => println("sdo: " + sdo))
        // print all individual sdo as json
        bundle.objects.foreach(sdo => println("sdo.asJson: " + sdo.asJson))
    }
    // alternatively
    // decode[Bundle](jsondoc).getOrElse(Bundle(List.empty))
  }
}
