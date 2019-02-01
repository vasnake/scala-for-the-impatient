organization := "book"
name := "scala-for-the-impatient"
version := "1.0.2-SNAPSHOT"

scalaVersion := "2.12.8"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
