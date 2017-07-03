
version := (version in ThisBuild).value

scalaVersion in ThisBuild := "2.12.2"

val playJsonVersion = "2.6.1"

lazy val root = project.in(file(".")).
  aggregate(scalastixJS, scalastixJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val scalastix = crossProject.in(file(".")).
  // common to both jvm and js
  settings(
  name := "scalastix",
  version := (version in ThisBuild).value,
  organization := "com.github.workingDog",
  homepage := Some(url("https://github.com/workingDog/scalastix")),
  licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  libraryDependencies ++= Seq(
    "org.threeten" % "threetenbp" % "1.3.5",
    "com.typesafe.play" %%% "play-json" % playJsonVersion
  //  "com.typesafe.play.extras" %%% "play-geojson" % "1.4.1"
  )
).
  jvmSettings(
    fork := true,
    javaOptions in compile += "-Xmx8G",
    javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xmx8G"),
    scalacOptions ++= Seq(
      "-deprecation",
      "-feature",
      "-unchecked",
      "-Xlint"
    )
  ).
  jsSettings(
    skip in packageJSDependencies := false,
    scalaJSStage in Global := FullOptStage,
    jsDependencies += RuntimeDOM,
    libraryDependencies ++= Seq(
      "org.scala-js" %%% "scalajs-dom" % "0.9.2",
      "org.scala-js" %%% "scalajs-java-time" % "0.2.1",
      "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-M12"
    )
  )

lazy val scalastixJVM = scalastix.jvm
lazy val scalastixJS = scalastix.js
