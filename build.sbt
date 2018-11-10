ThisBuild / name := "typecomet"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.12.7"

ThisBuild / libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

lazy val root = (project in file(".")) dependsOn macros

lazy val macros = project settings (
  scalacOptions ++= Seq("-language:experimental.macros"),
  libraryDependencies ++= Seq(
    scalaOrganization.value % "scala-reflect" % scalaVersion.value,
    scalaOrganization.value % "scala-compiler" % scalaVersion.value,
    "org.typelevel" %% "macro-compat" % "1.1.1",
    compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.patch)
  )
)