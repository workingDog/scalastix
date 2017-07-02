
name := "scalastix"

version := (version in ThisBuild).value

organization := "com.github.workingDog"

scalaVersion := "2.12.2"

crossScalaVersions := Seq("2.11.11", "2.12.2")

val playJsonVersion = "2.6.1"

// for scala.js .... see also plugins.sbt
//enablePlugins(ScalaJSPlugin)
//skip in packageJSDependencies := false
//scalaJSStage in Global := FastOptStage // FullOptStage //
//jsDependencies += RuntimeDOM
//libraryDependencies ++= Seq(
//  "org.scala-js" %%% "scalajs-dom" % "0.9.2",
//  "org.scala-js" %%% "scalajs-java-time" % "0.2.1",
//  "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-M12",
//  "org.threeten" % "threetenbp" % "1.3.5",
//  "com.typesafe.play" %%% "play-json" % playJsonVersion
//)

// for JVM scala
fork := true
javaOptions in compile += "-Xmx8G"
javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xmx8G")
libraryDependencies ++= Seq(
  "org.threeten" % "threetenbp" % "1.3.5",
//  "com.typesafe.play.extras" % "play-geojson_2.11" % "1.4.1",
  "com.typesafe.play" %% "play-json" % playJsonVersion
)

mainClass in assembly := Some("com.kodekutters.Example2")
assemblyJarName in assembly := "scalastix_2.12-0.3-SNAPSHOT.jar"

// common
scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-Xlint"
)

homepage := Some(url("https://github.com/workingDog/scalastix"))

licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))

resolvers += Resolver.sonatypeRepo("releases")

