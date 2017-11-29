resolvers += Resolver.sonatypeRepo("public")
resolvers += Resolver.sonatypeRepo("releases")
resolvers += Resolver.bintrayRepo("jroper", "maven")

// for scala.js
addSbtPlugin("org.scala-js" % "sbt-scalajs" % "0.6.20")

addSbtPlugin("com.github.gseitz" % "sbt-release" % "1.0.6")

addSbtPlugin("com.jsuereth" % "sbt-pgp" % "1.1.0")

addSbtPlugin("org.xerial.sbt" % "sbt-sonatype" % "2.0")

//addSbtPlugin("org.scala-js" % "sbt-jsdependencies" % "1.0.0-M1")