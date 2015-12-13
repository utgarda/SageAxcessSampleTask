import sbt.Keys._

name := "SageAxcessSampleTask"

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.11.7",
  libraryDependencies += "org.scalatest" %% "scalatest" % "2.2.5" % "test"
)

lazy val tokenizer = project.
  settings(commonSettings: _*)

lazy val plainscala = project.dependsOn(tokenizer).
  settings(commonSettings: _*)

lazy val akka = project.dependsOn(tokenizer).
  settings(commonSettings: _*).
  settings(
    libraryDependencies += "com.typesafe.akka" %% "akka-actor" % "2.4.1",
    libraryDependencies += "com.typesafe.akka" %% "akka-testkit" % "2.4.1"
  )
