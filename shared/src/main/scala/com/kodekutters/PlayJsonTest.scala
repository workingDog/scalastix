//package com.kodekutters
//
//import scala.language.implicitConversions
//import com.kodekutters.stix._
//import play.api.libs.json._
//
//import scala.io.Source
//import StixImplicits._
//import play.extras.geojson.{GeoJson, LatLng, Point}
//
//
///**
//  *
//  *
//  */
//object PlayJsonTest {
//
//  def main(args: Array[String]): Unit = {
////    test0()
////    test1()
////    test2()
////    test3()
//    test4()
//
//  //  test7()
//  }
//
//
//  def test4(): Unit = {
//    val te = new Location("Oceania",
//      new Address("Australia", "QLD", "Cairns", "4879"),
//      new Point[LatLng](new LatLng(-33.0, 151.0)))
//
//    val teJs = Json.toJson(te)
//    println("\nteJs: " + Json.prettyPrint(teJs))
//    println("\nteJs decode: " + teJs.as[Location])
//  }
//
//  def test0() = {
//
//    val te = new AttackPattern(name = "attack-name",
//      kill_chain_phases = Some(List(KillChainPhase("kill", "Bill"))),
//      external_references = Some(List(new ExternalReference("a-source-name"))),
//      object_marking_refs = Some(List(Identifier("xxxx")))
//    )
//
//    val teJs = Json.toJson(te)
//    println("\nteJs: " + teJs)
//    println("\nteJs decode: " + teJs.as[AttackPattern])
//
//    val js =
//      """{"type": "attack-pattern", "name" : "attack-name-x",
//          "id" : "attack-pattern--0fe33f18-9717-4329-9179-429d7304ef73",
//          "created": "2017-05-11T07:13:18.448Z",
//          "modified": "2017-05-11T07:13:18.448Z",
//      "kill_chain_phases": [
//         {
//           "kill_chain_name": "mandiant-attack-lifecycle-model",
//           "phase_name": "establish-foothold"
//         }
//       ]
//         }""".stripMargin
//    val prs = Json.parse(js)
//    println("\nprs: " + prs)
//
//    val obj = prs.as[AttackPattern]
//    println("\nobj: " + obj)
//  }
//
//  def test1(): Unit = {
//
//    val te = new TPLMarking(TLPlevels.red)
//
//    val teJs = Json.toJson(te)
//    println("\nteJs: " + teJs)
//    println("\nteJs decode: " + teJs.as[TPLMarking])
//
//    val te1 = new TPLMarking(TLPlevels("orange"))
//
//    val teJs1 = Json.toJson(te1)
//    println("\nteJs1: " + teJs1)
//    println("\nteJs1 decode: " + teJs1.as[TPLMarking])
//
//    val markingdef = new MarkingDefinition(MarkingDefinition.`type`,
//      Identifier(MarkingDefinition.`type`),
//      Timestamp.now(), "definitionxxxxtype", te)
//
//    val markingdefjs = Json.toJson(markingdef)
//    println("\nmarkingdefjs: " + markingdefjs)
//    println("\nmarkingdefjs decode: " + Json.fromJson[MarkingDefinition](markingdefjs))
//
//  }
//
//  def test2(): Unit = {
//
//    val te1 = new AttackPattern(name = "attack-name1",
//      kill_chain_phases = Some(List(KillChainPhase("kill", "Bill1"))),
//      external_references = Some(List(new ExternalReference("a-source-name1"))),
//      object_marking_refs = Some(List(Identifier("xxxx")))
//    )
//
//    val te2 = new AttackPattern(name = "attack-name2",
//      kill_chain_phases = Some(List(KillChainPhase("kill", "Bill2"))),
//      external_references = Some(List(new ExternalReference("a-source-name2"))),
//      object_marking_refs = Some(List(Identifier("yyyyy")))
//    )
//
//    val bundle = Bundle(te1, te2)
//
//    val bundlejs = Json.toJson(bundle)
//    println("\nbundlejs: " + bundlejs)
//    println("\nbundlejs decode: " + bundlejs.as[Bundle])
//
//  }
//
//  def test3(): Unit = {
//
//    // read a SITX document from a file
//    val fileDoc = Source.fromFile("./stixfiles/test1.json").mkString
//    //  println("fileDoc:\n"+fileDoc)
//
//    Json.parse(fileDoc).asOpt[Bundle] match {
//      case None => println("\n-----> invalid JSON ")
//      case Some(bundle) =>
//        println("\n-----> bundle: " + bundle)
//        val bundlejs = Json.toJson(bundle)
//        println("\n-----> bundlejs: " + Json.prettyPrint(Json.toJson(bundlejs)).toString)
//        //    println("\n-----> bundlejs: " + bundlejs)
//        println("\n-----> bundlejs decode: " + Json.fromJson[Bundle](bundlejs))
//        //  get all attackPattern
//        val allPat = bundle.objects.filter(_.`type` == AttackPattern.`type`)
//        allPat.foreach(x => println("\n-----> allPat: " + x))
//        allPat.foreach(x => println("\n-----> allPat json: " + Json.toJson(x)))
//    }
//
//  }
//
//  def test7() = {
//
//    val te = Indicator(pattern = "vvvvvv",
//      valid_from = Timestamp.now(),
//      name = Option(""),
//      valid_until = Option(Timestamp.now()),
//      labels = Option(List("malicious-activity"))
//    )
//
//    val teJs = Json.toJson(te)
//    println("\nteJs: " + teJs)
//    println("\nteJs decode: " + teJs.as[Indicator])
//
//    val js =
//      """ {
//            "type": "indicator",
//            "id": "indicator--8e2e2d2b-17d4-4cbf-938f-98ee46b3cd3f",
//            "created_by_ref": "identity--f431f809-377b-45e0-aa1c-6a4751cae5ff",
//            "created": "2016-04-06T20:03:48.000Z",
//            "modified": "2016-04-06T20:03:48.000Z",
//            "labels": ["malicious-activity"],
//            "name": "Poison Ivy Malware",
//            "description": "This file is part of Poison Ivy",
//            "pattern": "[ file.hashes.MD5 = '3773a88f65a5e780c8dff9cdc3a056f3' ]",
//            "valid_from": "2016-01-01T00:00:00Z",
//            "valid_until": "2016-01-01T00:00:00Z"
//          }""".stripMargin
//    val prs = Json.parse(js)
//    println("\nprs: " + prs)
//
//    val obj = prs.as[Indicator]
//    println("\nobj: " + obj)
//  }
//
//}