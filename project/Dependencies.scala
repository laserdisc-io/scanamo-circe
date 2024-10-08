import sbt.Keys.libraryDependencies
import sbt.*

//noinspection TypeAnnotation
object Dependencies {

  val Compat     = libraryDependencies += "org.scala-lang.modules" %% "scala-collection-compat" % "2.11.0"
  val Circe      = libraryDependencies += "io.circe"               %% "circe-parser"            % "0.14.9"
  val Scanamo    = libraryDependencies += "org.scanamo"            %% "scanamo"                 % "2.0.0"
  val ScalaTest  = libraryDependencies += "org.scalatest"          %% "scalatest"               % "3.2.17" % "test"
  val scalacheck = libraryDependencies += "org.scalacheck"         %% "scalacheck"              % "1.17.0" % "test"
}
