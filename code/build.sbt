name := "akka-stream-monix"

version := "0.1"

scalaVersion := "2.12.3"

//noinspection Annotator
libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream" % "2.5.3",
  "io.monix" %% "monix" % "2.3.0",
  "com.github.detro" % "ghostdriver" % "2.1.0",
  "com.github.kxbmap" %% "configs" % "0.4.4",
  "io.github.andrebeat" %% "scala-pool" % "0.4.0",
  "com.softwaremill.sttp" %% "core" % "1.0.2",
  "com.softwaremill.sttp" %% "akka-http-backend" % "1.0.2",
  "com.typesafe.akka" %% "akka-http" % "10.0.10",
  "com.softwaremill.sttp" %% "async-http-client-backend-monix" % "1.0.2",
  "com.ning" % "async-http-client" % "1.9.40",
  "com.softwaremill.macwire" %% "macros" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "macrosakka" % "2.3.0" % "provided",
  "com.softwaremill.macwire" %% "util" % "2.3.0",
  "com.softwaremill.macwire" %% "proxy" % "2.3.0",
  "com.lihaoyi" %% "utest" % "0.5.4" % "test"
)

testFrameworks += new TestFramework("utest.runner.Framework")

resolvers += "jitpack.io" at "https://jitpack.io"