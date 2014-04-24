scalaVersion := "2.11.0"

// Uncomment to test with a locally built copy of Scala.
// scalaHome := Some(file("/code/scala2/build/pack"))
resolvers ++= (if (scalaVersion.value.endsWith("SNAPSHOT")) List(Resolver.sonatypeRepo("snapshots")) else Nil)

organization := "org.scala-lang.modules"

name := "project2"

version := "0.1"

scalacOptions in compile ++= Seq("-optimize", "-unchecked", "-Xlint", "-feature")
