version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.8"

name := "BatteryBot"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig"                      % "0.17.1",
  "org.flywaydb"          % "flyway-core"                      % "8.5.2",
  "org.tpolecat"          %% "doobie-core"                     % "1.0.0-RC1",
  "org.tpolecat"          %% "doobie-postgres"                 % "1.0.0-RC1",
  "org.typelevel"         %% "munit-cats-effect-3"             % "1.0.7"      % Test,
  "com.dimafeng"          %% "testcontainers-scala-munit"      % "0.40.3"     % Test,
  "com.dimafeng"          %% "testcontainers-scala-postgresql" % "0.40.3"     % Test,
  "org.tpolecat"          %% "doobie-munit"                    % "1.0.0-RC2"  % Test
)
