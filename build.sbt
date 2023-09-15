lazy val scala213 = "2.13.10"
lazy val scala3   = "3.2.2"

lazy val root = project
  .in(file("."))
  .settings(
    name               := "scanamo-circe",
    organization       := "io.laserdisc",
    crossScalaVersions := List(scala213, scala3),
    scalaVersion       := scala3,
    licenses ++= Seq(("MIT", url("http://opensource.org/licenses/MIT"))),
    homepage := Some(url("https://github.com/laserdisc-io/scanamo-circe")),
    developers := List(
      Developer("semenodm", "Dmytro Semenov", "", url("https://github.com/semenodm")),
      Developer("barryoneill", "Barry O'Neill", "", url("https://github.com/barryoneill"))
    ),
    Test / fork := true,
    scalacOptions ++= Seq(
      "-deprecation",
      "-encoding",
      "UTF-8",
      "-feature",
      "-language:existentials,experimental.macros,higherKinds,implicitConversions,postfixOps",
      "-unchecked",
      "-Xfatal-warnings"
    ),
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((2, minor)) if minor >= 13 =>
          Seq(
            "-Xlint:-unused,_",
            "-Ywarn-numeric-widen",
            "-Ywarn-value-discard",
            "-Ywarn-unused:implicits",
            "-Ywarn-unused:imports",
            "-Xsource:3",
            "-Xlint:-byname-implicit",
            "-P:kind-projector:underscore-placeholders",
            "-Xlint",
            "-Ywarn-macros:after"
          )
        case _ => Seq.empty
      }
    },
    scalacOptions ++= {
      CrossVersion.partialVersion(scalaVersion.value) match {
        case Some((3, _)) =>
          Seq(
            "-Ykind-projector:underscores",
            "-source:future",
            "-language:adhocExtensions",
            "-Wconf:msg=`= _` has been deprecated; use `= uninitialized` instead.:s"
          )
        case _ => Seq.empty
      }
    },
    sbt.Test / testFrameworks += TestFrameworks.ScalaCheck,
    libraryDependencies ++= Seq(
      compilerPlugin(("org.typelevel" %% "kind-projector" % "0.13.2").cross(CrossVersion.full)),
      compilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
    ).filterNot(_ => scalaVersion.value.startsWith("3.")),
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
