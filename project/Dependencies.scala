import sbt.Keys.libraryDependencies
import sbt.*

//noinspection TypeAnnotation
object Dependencies {

  val Compat     = libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.9.0"
  val Circe      = libraryDependencies += "io.circe"               %% "circe-parser"            % "0.14.5"
  val Scanamo    = libraryDependencies += "org.scanamo"            %% "scanamo"                 % "1.0.0-M23"
  val ScalaTest  = libraryDependencies += "org.scalatest"          %% "scalatest"               % "3.2.15" % "test"
  val scalacheck = libraryDependencies += "org.scalacheck"         %% "scalacheck"              % "1.17.0" % "test"
}
