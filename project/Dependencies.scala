import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val undertow = "io.undertow" % "undertow-core" % "1.4.12.Final"
  lazy val quill = "io.getquill" %% "quill-jdbc" % "2.3.1"
  lazy val markdown = "com.atlassian.commonmark" % "commonmark" % "0.10.0"
  lazy val flyway = "org.flywaydb" % "flyway-core" % "5.0.4"
  lazy val cache = "com.github.cb372" %% "scalacache-caffeine" % "0.22.0"
  lazy val auth = "org.mindrot" % "jbcrypt" % "0.4"
  lazy val akkaHttp = "com.typesafe.akka" %% "akka-http" % "10.1.5"
  lazy val akka = "com.typesafe.akka" %% "akka-actor" % "2.5.16"
  lazy val akkaStreams = "com.typesafe.akka" %% "akka-stream" % "2.5.16"

  val circeVersion = "0.9.0"
  lazy val json = Seq(
    "io.circe" %% "circe-core",
    "io.circe" %% "circe-generic",
    "io.circe" %% "circe-parser"
  ).map(_ % circeVersion)

  lazy val sashimiDependencies: Seq[ModuleID] =
    Seq(scalaTest, undertow, quill, markdown, cache, flyway, auth, akka, akkaHttp, akkaStreams) ++ json
}
