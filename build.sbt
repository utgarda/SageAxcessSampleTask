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
