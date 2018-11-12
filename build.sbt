ThisBuild / name := "typecomet"

ThisBuild / version := "0.1"

ThisBuild / scalaVersion := "2.12.7"

ThisBuild / libraryDependencies ++= Seq(
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test"
)

lazy val root = (project in file(".")) dependsOn macros settings (
  scalacOptions ++= Seq(
    // "-Xprint:typer"
  ),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)

lazy val macros = project settings (
  scalacOptions ++= Seq(
    "-language:experimental.macros"
  ),
  libraryDependencies ++= Seq(
    "org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.1",
    scalaOrganization.value % "scala-reflect" % scalaVersion.value,
    scalaOrganization.value % "scala-compiler" % scalaVersion.value
  ),
  addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
)