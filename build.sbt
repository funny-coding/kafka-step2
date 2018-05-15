name := "kafka-step2"

version := "0.1"

scalaVersion := "2.12.6"

libraryDependencies ++= Seq(
  "org.apache.kafka" % "kafka-streams" % "1.1.0",
  "com.typesafe" % "config" % "1.3.3"
//  "org.scalatest" % "scalatest" % "3.0.1"
)