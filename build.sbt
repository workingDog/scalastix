
name := "scalastix"

version := (version in ThisBuild).value

scalaVersion in ThisBuild := "2.13.3"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.9.0"
)

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

organization := "com.github.workingDog"

homepage := Some(url("https://github.com/workingDog/scalastix"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))


