import laserdisc.sbt.CompileTarget.Scala2And3
import laserdisc.sbt.LaserDiscDevelopers.*

ThisBuild / laserdiscRepoName      := "scanamo-circe"
ThisBuild / laserdiscCompileTarget := Scala2And3

lazy val root = project
  .in(file("."))
  .settings(
    name        := "scanamo-circe",
    developers  := List(Dmytro, Barry),
    Dependencies.Compat,
    Dependencies.Circe,
    Dependencies.Scanamo,
    Dependencies.ScalaTest,
    Dependencies.scalacheck
  )
  .enablePlugins(LaserDiscDefaultsPlugin)
