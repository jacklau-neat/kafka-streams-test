ThisBuild / scalaVersion := "2.12.7"
ThisBuild / organization := "confluent"

resolvers +=
  "Confluent" at "https://packages.confluent.io/maven/"

lazy val xero = (project in file("."))
  .settings(
    name := "Xero",
    libraryDependencies ++= Seq(
      "org.apache.kafka" %% "kafka-streams-scala" % "2.0.1",
      "com.lightbend" %% "kafka-streams-scala" % "0.2.1",
      "com.typesafe.play" %% "play-json" % "2.6.8"
    )
  )
