package com.kodekutters

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

import scala.io.Source
import com.kodekutters.stix._
import io.circe.Printer

/**
  * a simple example
  */
object Example1 {
  def main(args: Array[String]): Unit = {
    // to remove the output of "null" for empty fields
    implicit val myPrinter = Printer.spaces2.copy(dropNullKeys = true)

    // read a STIX bundle from a file
    val jsondoc = Source.fromFile("./stixfiles/test1.json").mkString
    // create a bundle object from it
    decode[Bundle](jsondoc) match {
      case Left(failure) => println("\n-----> invalid JSON ")
      case Right(bundle) =>
        println("\n-----> bundle: " + bundle)
        // back to json
        println("\n-----> bundle.asJson: " + myPrinter.pretty(bundle.asJson))
        // print all individual sdo
        bundle.objects.foreach(sdo => println("sdo: " + sdo))
        // print all individual sdo as json
        bundle.objects.foreach(sdo => println("sdo.asJson: " + myPrinter.pretty(sdo.asJson)))
        //  get all attackPattern
        val allPat = bundle.objects.filter(_.`type` == AttackPattern.`type`)
        allPat.foreach(x => println("\n-----> allPat: " + x))
        allPat.foreach(x => println("\n-----> allPat json: " + x.asJson))
    }
  }
}
