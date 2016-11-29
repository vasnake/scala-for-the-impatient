name := "bookchapters"

version := "1.0"

scalaVersion := "2.12.0"

val depsAkka = Seq(
    "com.typesafe.akka" %% "akka-actor" % "2.4.14",
    "com.typesafe.akka" %% "akka-testkit" % "2.4.14"
)

val depsReactive = Seq(
    "io.reactivex" %% "rxscala" % "0.26.4"
)

resolvers += "Sonatype OSS Snapshots" at
    "https://oss.sonatype.org/content/repositories/releases"

libraryDependencies ++= Seq(
    "com.storm-enroute" %% "scalameter-core" % "0.8.2"
) ++ depsAkka ++ depsReactive

scalacOptions ++= Seq("-deprecation", "-feature")
