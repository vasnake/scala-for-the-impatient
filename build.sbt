organization := "book"
name := "scala-for-the-impatient"
version := "1.0.2-SNAPSHOT"

scalaVersion := "2.12.8"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
    "io.reactivex" % "rxscala_2.12" % "0.26.5",
    "com.storm-enroute" %% "scalameter" % "0.10.1",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
