name := "SensorStatisticsSrv"

version := "0.1"

scalaVersion := "2.13.10"

idePackagePrefix := Some("com.ubs.tri")

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.9"
libraryDependencies += "org.scalactic" %% "scalactic" % "3.2.15"
libraryDependencies +=
  "org.scalatest" %% "scalatest" % "3.2.11" % Test
libraryDependencies += "org.scalamock" %% "scalamock" % "5.1.0" % Test

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
