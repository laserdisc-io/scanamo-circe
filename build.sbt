lazy val scala212 = "2.12.13"
lazy val scala213 = "2.13.6"

def versionSpecificOptions(scalaVersion: String) =
  CrossVersion.partialVersion(scalaVersion) match {
    case Some((2, 12)) => Seq("-Ypartial-unification")
    case _             => Seq.empty
  }

lazy val root = project
  .in(file("."))
  .settings(
    name := "scanamo-circe",
    organization := "io.laserdisc",
    crossScalaVersions := List(scala212, scala213),
    scalaVersion := scala213,
    licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
    homepage := Some(url("https://github.com/laserdisc-io/scanamo-circe")),
    developers := List(
      Developer("semenodm", "Dmytro Semenov", "", url("https://github.com/semenodm")),
      Developer("barryoneill", "Barry O'Neill", "", url("https://github.com/barryoneill"))
    ),
    Test / fork := true,
    scalacOptions := versionSpecificOptions(scalaVersion.value),
    scalacOptions ++= Seq(
      "-encoding",
      "UTF-8", // source files are in UTF-8
      "-deprecation", // warn about use of deprecated APIs
      "-unchecked", // warn about unchecked type parameters
      "-feature", // warn about misused language features
      "-language:higherKinds", // allow higher kinded types without `import scala.language.higherKinds`
      "-language:implicitConversions", // allow use of implicit conversions
      "-language:postfixOps", // postfix ops
      "-Xlint", // enable handy linter warnings
      "-Xfatal-warnings", // turn compiler warnings into errors
      "-Ywarn-macros:after" // allows the compiler to resolve implicit imports being flagged as unused
    ),
    sbt.Test / testFrameworks += TestFrameworks.ScalaCheck,
    addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1"),
    addCompilerPlugin("org.typelevel" %% "kind-projector" % "0.10.3"),
    Dependencies.Compat,
    Dependencies.Circe,
    Dependencies.Scanamo,
    Dependencies.ScalaTest,
    Dependencies.scalacheck,
    addCommandAlias("format", ";scalafmtAll;scalafmtSbt"),
    addCommandAlias("checkFormat", ";scalafmtCheckAll;scalafmtCheck"),
    addCommandAlias("build", ";checkFormat;clean;test;coverage"),
    addCommandAlias("release", ";checkFormat;clean;test;coverage;release")
  )
  .enablePlugins(ScalafmtPlugin)
