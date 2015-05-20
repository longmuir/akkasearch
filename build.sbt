name := "akkasearch"

version := "1.0"

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-stream-experimental" % "1.0-RC2",
  "com.typesafe.akka" %% "akka-http-core-experimental" % "1.0-RC2",
  "com.typesafe.play" %% "play-json" % "2.3.4",
  "com.typesafe.akka" %% "akka-actor" % "2.3.11",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.11",
  "org.scala-lang.modules" %% "scala-async" % "0.9.2",
  "org.scalatest" % "scalatest_2.11" % "2.2.4" % "test"
)

    