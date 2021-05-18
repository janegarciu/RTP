package tcpserver

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}

object Application extends App{
  var inetSocketAddress = new InetSocketAddress("localhost", 8080)
  val system = ActorSystem("app")
  val server = system.actorOf(Props(new Server(inetSocketAddress)), "server")
}
