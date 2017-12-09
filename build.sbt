
name := "scalastix"

version := (version in ThisBuild).value

scalaVersion in ThisBuild := "2.12.4"

libraryDependencies ++= Seq(
  "com.typesafe.play" %% "play-json" % "2.6.7"
)

javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xmx8G")

scalacOptions ++= Seq("-deprecation", "-feature", "-unchecked", "-Xlint")

organization := "com.github.workingDog"

homepage := Some(url("https://github.com/workingDog/scalastix"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))


