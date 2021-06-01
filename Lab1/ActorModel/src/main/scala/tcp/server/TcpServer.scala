package tcp.server

import java.net.InetSocketAddress

import akka.actor.{Actor, ActorRef, Props}
import akka.io.{IO, Tcp}
import akka.util.ByteString

object TcpServer {
  def props(remote: InetSocketAddress, handler: ActorRef) =
    Props(new TcpServer(remote,handler))
}

class TcpServer(remote: InetSocketAddress,handler: ActorRef) extends Actor {

  import akka.io.Tcp._
  import context.system

  IO(Tcp) ! Bind(self, remote)

  def receive = {
    case b @ Bound(localAddress) =>
      context.parent ! b

    case CommandFailed(_: Bind) ⇒ context stop self

    case c @ Connected(remote, local) =>
      println(s"Client connected - Remote(Client): ${remote.getAddress} Local(Server): ${local.getAddress}")

      val connection = sender()
      connection ! Register(handler)
    case data: ByteString =>
//      println("Sending request data to handler: " + data.utf8String)

  }

}