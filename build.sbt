version := "0.1.0-SNAPSHOT"

scalaVersion := "2.13.8"

name := "BatteryBot"

val http4sVersion = "0.23.11"

libraryDependencies ++= Seq(
  "com.github.pureconfig" %% "pureconfig"                      % "0.17.1",
  "org.flywaydb"           % "flyway-core"                     % "8.5.2",
  "org.tpolecat"          %% "doobie-core"                     % "1.0.0-RC1",
  "org.tpolecat"          %% "doobie-postgres"                 % "1.0.0-RC1",
  "org.http4s"            %% "http4s-dsl"                      % http4sVersion,
  "org.http4s"            %% "http4s-ember-server"             % http4sVersion,
  "org.http4s"            %% "http4s-ember-client"             % http4sVersion,
  "org.http4s"            %% "http4s-circe"                    % http4sVersion,
  "org.jsoup"              % "jsoup"                           % "1.14.3",
  "io.circe"              %% "circe-core"                      % "0.14.1",
  "io.circe"              %% "circe-generic"                   % "0.14.1",
  "io.circe"              %% "circe-generic-extras"            % "0.14.1",
  "io.circe"              %% "circe-literal"                   % "0.14.1"    % Test,
  "org.typelevel"         %% "munit-cats-effect-3"             % "1.0.7"     % Test,
  "com.dimafeng"          %% "testcontainers-scala-munit"      % "0.40.3"    % Test,
  "com.dimafeng"          %% "testcontainers-scala-postgresql" % "0.40.3"    % Test,
  "org.tpolecat"          %% "doobie-munit"                    % "1.0.0-RC2" % Test
)
