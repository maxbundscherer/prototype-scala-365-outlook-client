name := "prototype-365-outlook-client"
version := "0.1"
scalaVersion := "2.12.8"

/* ####### Dependencies ####### */

// Scala HTTP Client (sttp)
libraryDependencies += "com.softwaremill.sttp" %% "core" % "1.5.17"

// Scala JSON Parser (circe)
val circeVersion = "0.10.0"
libraryDependencies ++= Seq(
  "io.circe" %% "circe-core",
  "io.circe" %% "circe-generic",
  "io.circe" %% "circe-parser"
).map(_ % circeVersion)

// Scala Config-Factory (lightbend-config)
libraryDependencies += "com.typesafe" % "config" % "1.3.4"

// ScribeJava (OAuth Client)
libraryDependencies += "com.github.scribejava" % "scribejava-apis" % "6.5.1"

// Akka HTTP and STREAMS (process OAuth-Callback)
libraryDependencies += "com.typesafe.akka" %% "akka-http" % "10.1.8"
libraryDependencies += "com.typesafe.akka" %% "akka-stream" % "2.5.23"