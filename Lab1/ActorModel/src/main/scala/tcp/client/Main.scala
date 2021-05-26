package tcp.client

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString

object Main {
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 9900
    println(s"Started client! connecting to ${host}:${port}")

    val clientProps = TcpClient.props(new InetSocketAddress(host, port), null)

    val actorSystem: ActorSystem = ActorSystem.create("myClientActorSystem")
    val clientActor: ActorRef = actorSystem.actorOf(clientProps)

    Thread.sleep(2000)
    clientActor ! ByteString("hello from client")

  }
}
