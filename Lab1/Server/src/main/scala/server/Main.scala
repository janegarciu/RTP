package server

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString

object Main {
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 9900
    println(s"Server started! listening on ${host}:${port}")
    val actorSystem: ActorSystem = ActorSystem.create("myServerActorSystem")
    val handler = actorSystem.actorOf(Props[SimplisticHandler])
    val serverProps = TcpServer.props(new InetSocketAddress(host, port), handler)

    val serverActor: ActorRef = actorSystem.actorOf(serverProps)
    serverActor ! ByteString("Starting server...")
  }
}
