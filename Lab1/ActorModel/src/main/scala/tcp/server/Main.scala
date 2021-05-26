package tcp.server

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem}
import akka.util.ByteString

object Main {
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 9900
    println(s"Server started! listening to ${host}:${port}")

    val serverProps = TcpServer.props(new InetSocketAddress(host, port))
    val actorSystem: ActorSystem = ActorSystem.create("myServerActorSystem")
    val serverActor: ActorRef = actorSystem.actorOf(serverProps)
    serverActor ! ByteString("Starting server...")
  }
}
