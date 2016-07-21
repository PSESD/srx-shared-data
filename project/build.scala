import sbt.Keys._
import sbt._

object SrxSharedDataBuild extends Build {

  lazy val srxSharedCore = RootProject(uri("https://github.com/PSESD/srx-shared-core.git"))

  lazy val hikariVersion = "2.4.7"
  lazy val scalaTestVersion = "2.2.6"

  lazy val project = Project("srx-shared-data", file("."))
    .settings(
      name := "srx-shared-data",
      version := "1.0",
      scalaVersion := "2.11.8",
      libraryDependencies ++=Seq(
        "com.zaxxer" % "HikariCP" % hikariVersion,
        "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    )
  ).dependsOn(srxSharedCore)

}