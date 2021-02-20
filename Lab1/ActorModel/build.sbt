
name := "ActorModel"

version := "0.1"

scalaVersion := "2.13.4"

val AkkaVersion = "2.5.31"
val AkkaHttpVersion = "10.1.11"
libraryDependencies ++= Seq(
  "com.lightbend.akka" %% "akka-stream-alpakka-sse" % "2.0.2",
  "com.typesafe.akka" %% "akka-stream" % AkkaVersion,
  "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion
)
libraryDependencies += "com.typesafe.akka" %% "akka-actor-typed" % AkkaVersion