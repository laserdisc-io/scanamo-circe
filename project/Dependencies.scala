import sbt.Keys.libraryDependencies
import sbt._

//noinspection TypeAnnotation
object Dependencies {

  val Compat     = libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.8.1"
  val Circe      = libraryDependencies += "io.circe"               %% "circe-parser"            % "0.14.3"
  val Scanamo    = libraryDependencies += "org.scanamo"            %% "scanamo"                 % "1.0.0-M20"
  val ScalaTest  = libraryDependencies += "org.scalatest"          %% "scalatest"               % "3.2.14" % "test"
  val scalacheck = libraryDependencies += "org.scalacheck"         %% "scalacheck"              % "1.17.0" % "test"
}
