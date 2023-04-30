name := "SensorStatisticsSrv"

version := "0.1"

scalaVersion := "2.13.10"

idePackagePrefix := Some("com.ubs.tri")

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.4.9"

addCompilerPlugin("com.olegpy" %% "better-monadic-for" % "0.3.1")
