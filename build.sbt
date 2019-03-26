name := "teste-gympass"

version := "0.1"

scalaVersion := "2.12.8"

libraryDependencies += "org.typelevel" %% "cats-core" % "1.6.0"
libraryDependencies += "org.typelevel" %% "cats-free" % "1.6.0"

mainClass in assembly := Some("br.com.gympass.Main")

