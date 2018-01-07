import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val colossus = "com.tumblr" %% "colossus" % "0.10.1"
  lazy val undertow = "io.undertow" % "undertow-core" % "1.4.12.Final"
  lazy val quill = "io.getquill" %% "quill-jdbc" % "2.3.1"
  lazy val redis = "redis.clients" % "jedis" % "2.9.0"
  lazy val markdown = "com.atlassian.commonmark" % "commonmark" % "0.10.0"
  lazy val flyway = "org.flywaydb" % "flyway-core" % "5.0.4"

  lazy val sashimiDependencies = Seq(scalaTest, colossus, undertow, quill, redis, markdown, flyway)
}
