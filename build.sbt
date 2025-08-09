ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.16"

ThisBuild / run / fork := true

lazy val root = (project in file("."))
  .settings(
    name := "settle-debts",
    libraryDependencies ++= Seq(
      "org.http4s" %% "http4s-ember-server" % "0.23.26",
      "org.http4s" %% "http4s-dsl" % "0.23.26",
      "io.circe" %% "circe-core" % "0.14.7",
      "io.circe" %% "circe-generic" % "0.14.7",
      "io.circe" %% "circe-parser" % "0.14.7",
      "org.http4s" %% "http4s-circe" % "0.23.26",
      "org.typelevel" %% "cats-effect" % "3.5.4",
      "org.scalatest" %% "scalatest" % "3.2.19" % Test,
      "org.slf4j" % "slf4j-simple" % "2.0.16"
    ),

    Compile / run / javaOptions += "-Dcats.effect.warnOnNonMainThreadDetected=false"
  )
