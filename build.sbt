organization := "com.github.philcali"

name := "simple-engine"

version := "0.0.1"

scalaVersion := "2.9.1"

crossScalaVersions := Seq("2.8.0", "2.8.1", "2.8.2", "2.9.0", "2.9.0-1", "2.9.1")

libraryDependencies ++= Seq(
  "com.google.appengine" % "appengine-api-1.0-sdk" % "1.5.5",
  "com.google.appengine" % "appengine-api-stubs" % "1.5.5",
  "com.google.appengine" % "appengine-api-labs" % "1.5.5",
  "com.google.appengine" % "appengine-testing" % "1.5.5" % "test",
)
