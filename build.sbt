import Dependencies._

lazy val commonSettings = Seq[Setting[_]](
  organization := "net.anopara",
  scalaVersion := "2.12.3",
  version := "0.1.0-SNAPSHOT",
  parallelExecution in Test := false,
  test in assembly := {},
  scalacOptions := List(
    "-feature",
    "-unchecked",
    "-deprecation",
    "-target:jvm-1.8"
  )
)

lazy val noPubSettings = commonSettings ++ Seq(
  publishArtifact := false,
  publishTo := Some(Resolver.file("Unused transient repository", file("target/unusedrepo")))
)

lazy val root = Project(id = "root", base = file("."))
  .settings(noPubSettings: _*)
  .configs(IntegrationTest)
  .dependsOn(sashimi)
  .aggregate(sashimi, sample)

lazy val sashimi = (project in file("sashimi"))
  .settings(commonSettings: _*)
  .enablePlugins(SbtTwirl)
  .settings(
    name := "sashimi",
    mainClass in assembly := Some("net.anopara.sample.sashimi.SampleApp"),
    assemblyJarName in assembly := "sample.jar",
    libraryDependencies ++= sashimiDependencies ++ Seq(
      "org.slf4j" % "slf4j-log4j12" % "1.7.25",
      "org.mariadb.jdbc" % "mariadb-java-client" % "2.2.1"
    )
  )

lazy val sample = (project in file("sample"))
  .settings(commonSettings: _*)
  .settings(
    name := "sashimi-sample",
    libraryDependencies += scalaTest % Test
  )
  .dependsOn(sashimi)
  .enablePlugins(SbtTwirl)


