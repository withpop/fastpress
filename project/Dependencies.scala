import sbt._

object Dependencies {
  lazy val scalaTest = "org.scalatest" %% "scalatest" % "3.0.3"
  lazy val undertow = "io.undertow" % "undertow-core" % "1.4.12.Final"
  lazy val quill = "io.getquill" %% "quill-jdbc" % "2.3.1"
  lazy val markdown = "com.atlassian.commonmark" % "commonmark" % "0.10.0"
  lazy val flyway = "org.flywaydb" % "flyway-core" % "5.0.4"
  lazy val cache = "com.github.cb372" %% "scalacache-caffeine" % "0.22.0"
  lazy val auth = Seq(
    "org.pac4j" % "undertow-pac4j" % "1.2.3",
    "org.pac4j" % "pac4j-sql" % "2.2.1",
    "org.pac4j" % "pac4j-http" % "2.2.1",
    "org.mindrot" % "jbcrypt" % "0.4"
  )

  lazy val sashimiDependencies =
    Seq(scalaTest, undertow, quill, markdown, cache, flyway) ++
      auth
}
