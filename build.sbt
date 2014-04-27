

name := "RingMailBox"

version := "1.0"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.3.1",
  "org.scalatest" % "scalatest_2.10" % "2.1.0" % "test",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.1"
)

scalaVersion := "2.10.4"
