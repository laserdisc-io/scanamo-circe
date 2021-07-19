import sbt.Keys.libraryDependencies
import sbt._

object Dependencies {

  val Compat = Seq(
    libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.5.0"
  )
  val Circe     = Seq(libraryDependencies += "io.circe"      %% "circe-parser" % "0.13.0")
  val Scanamo   = Seq(libraryDependencies += "org.scanamo"   %% "scanamo"      % "1.0.0-M15")
  val ScalaTest = Seq(libraryDependencies += "org.scalatest" %% "scalatest"    % "3.2.9" % "test")

}
