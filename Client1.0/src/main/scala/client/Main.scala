package client

import java.net.InetSocketAddress

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.ByteString

import scala.io.StdIn

object Main {
  def main(args: Array[String]): Unit = {
    val host = "localhost"
    val port = 9900
    println(s"Started client! connecting to ${host}:${port}")

    val actorSystem: ActorSystem = ActorSystem.create("myClientActorSystem")
    val clientProps = TcpClient.props(new InetSocketAddress(host, port), null)
    val clientActor: ActorRef = actorSystem.actorOf(clientProps)
    val clientListener = actorSystem.actorOf(Props(new ClientListener(clientActor)), name = "clientListener")

    var msg = StdIn.readLine()
    while (!msg.equals("stop")) {
      clientListener ! msg
      msg = StdIn.readLine()
    }

    Thread.sleep(2000)
    clientActor ! ByteString("Client up and running")

  }
}
