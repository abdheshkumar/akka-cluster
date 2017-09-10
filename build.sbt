name := "Akka-Cluster"

version := "0.1"

scalaVersion := "2.12.3"

val akkaVersion = "2.5.4"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-remote" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-metrics" % akkaVersion,
  "com.typesafe.akka" %% "akka-cluster-tools" % akkaVersion,
  "com.typesafe.akka" %% "akka-multi-node-testkit" % akkaVersion,
  "org.scalatest" %% "scalatest" % "3.0.1" % Test
)



//mainClass in Compile := Some("sample.pingpong.PingPongApp")

//dockerEntrypoint := Seq("bin/%s" format executableScriptName.value)

enablePlugins(JavaAppPackaging)
enablePlugins(DockerPlugin)

dockerEntrypoint := Seq("bin/ping-actor","bin/pong-actor")

maintainer in Docker := "Abdhesh Kumar <abdhesh.mca@gmail.com>"
packageSummary in Docker := "A small docker application"
packageDescription := "Docker [micro|nano] Service"
// exposing the play ports
//dockerExposedPorts := Seq(9000)
// Only add this if you want to rename your docker image name
packageName in Docker := "akka-cluster"