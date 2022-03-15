version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.8"

name := "BatteryBot"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig" % "0.17.1",
  "org.flywaydb" % "flyway-core" % "8.5.2",
  "org.tpolecat" %% "doobie-core"      % "1.0.0-RC1",
  "org.tpolecat" %% "doobie-postgres"  % "1.0.0-RC1"
)
