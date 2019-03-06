organization := "book"
name := "scala-for-the-impatient"
version := "1.0.2-SNAPSHOT"

scalaVersion := "2.12.8"
scalacOptions ++= Seq("-deprecation", "-feature")

libraryDependencies ++= Seq(
    "io.reactivex" % "rxscala_2.12" % "0.26.5",
    "com.storm-enroute" %% "scalameter" % "0.10.1",
    // annotations chapter
    "org.hibernate.javax.persistence" % "hibernate-jpa-2.0-api" % "1.0.1.Final",
    "javax.inject" % "javax.inject" % "1",
    "org.checkerframework" % "checker-qual" % "2.7.0",
    "com.novocode" % "junit-interface" % "0.11", // % Test
    // tests
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)
