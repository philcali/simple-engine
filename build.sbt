organization := "com.github.philcali"

name := "simple-engine"

version := "0.0.1"

scalaVersion := "2.9.1"

crossScalaVersions := Seq("2.8.1", "2.8.2", "2.9.0", "2.9.0-1", "2.9.1", "2.9.2")

libraryDependencies ++= Seq(
  "com.google.appengine" % "appengine-api-1.0-sdk" % "1.6.4" % "provided",
  "com.google.appengine" % "appengine-api-stubs" % "1.6.4" % "provided",
  "com.google.appengine" % "appengine-api-labs" % "1.6.4" % "provided",
  "com.google.appengine" % "appengine-testing" % "1.6.4" % "test",
  "org.scalatest" %% "scalatest" % "1.7.2" % "test"
)
