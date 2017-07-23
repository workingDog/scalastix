
import ReleaseTransformations._

name := "scalastix"

version := (version in ThisBuild).value

scalaVersion in ThisBuild := "2.12.2"

val playJsonVersion = "2.6.2"

lazy val root = project.in(file(".")).
  aggregate(scalastixJS, scalastixJVM).
  enablePlugins(ScalaJSPlugin).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val scalastix = crossProject.in(file(".")).
  // common to both jvm and js
  settings(
  version := (version in ThisBuild).value,
  organization := "com.github.workingDog",
  homepage := Some(url("https://github.com/workingDog/scalastix")),
  licenses := Seq("Apache 2" -> url("http://www.apache.org/licenses/LICENSE-2.0")),
  libraryDependencies ++= Seq(
    "org.threeten" % "threetenbp" % "1.3.6",
    "com.typesafe.play" %%% "play-json" % playJsonVersion
    //  "com.typesafe.play.extras" %%% "play-geojson" % "1.4.1"
  ),
  pomExtra := {
    <scm>
      <url>https://github.com/workingDog/scalastix</url>
      <connection>scm:git:git@github.com:workingDog/scalastix.git</connection>
    </scm>
      <developers>
        <developer>
          <id>workingDog</id>
          <name>Ringo Wathelet</name>
          <url>https://github.com/workingDog</url>
        </developer>
      </developers>
  },
  pomIncludeRepository := { _ => false },
  // Release settings
  sonatypeProfileName := "com.github.workingDog",
  releasePublishArtifactsAction := PgpKeys.publishSigned.value,
  releaseCrossBuild := true,
  releaseTagName := (version in ThisBuild).value,
  releaseProcess := Seq[ReleaseStep](
    checkSnapshotDependencies,
    inquireVersions,
    setReleaseVersion,
    commitReleaseVersion,
    tagRelease,
    publishArtifacts,
    ReleaseStep(action = Command.process("publishSigned", _), enableCrossBuild = true),
    setNextVersion,
    commitNextVersion,
    ReleaseStep(action = Command.process("sonatypeReleaseAll", _), enableCrossBuild = true),
    pushChanges
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
      "org.scala-js" %%% "scalajs-dom" % "0.9.3",
      "org.scala-js" %%% "scalajs-java-time" % "0.2.2",
      "io.github.cquiroz" %%% "scala-java-time" % "2.0.0-M12"
    )
  )

lazy val scalastixJVM = scalastix.jvm
lazy val scalastixJS = scalastix.js
