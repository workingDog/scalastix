
name := "scalastix"

version := (version in ThisBuild).value

scalaVersion in ThisBuild := "2.12.10"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.7.4"
)

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

organization := "com.github.workingDog"

homepage := Some(url("https://github.com/workingDog/scalastix"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))


