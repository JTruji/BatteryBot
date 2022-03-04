ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.8"

libraryDependencies += "com.github.pureconfig" %% "pureconfig" % "0.17.1"

lazy val root = (project in file("."))
  .settings(
    name := "BatteryBot"
  )
