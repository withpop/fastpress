import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val colossus = "com.tumblr" %% "colossus" % "0.10.1"
  lazy val quill = "io.getquill" %% "quill-jdbc" % "2.3.1"
  lazy val redis = "redis.clients" % "jedis" % "2.9.0"
  lazy val markdown = "com.atlassian.commonmark" % "commonmark" % "0.10.0"

  lazy val sashimiDependencies = Seq(scalaTest, colossus, quill, redis, markdown)
}
