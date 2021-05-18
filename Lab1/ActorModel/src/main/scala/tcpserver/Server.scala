package tcpserver
import akka.actor.{Actor, Props}
import akka.io.{IO, Tcp}
import java.net.InetSocketAddress

import actormodel.{Tweet, UpdatedUser}


class Server(inetSocketAddress: InetSocketAddress) extends Actor {
  import Tcp._
  import context.system
  IO(Tcp) ! Bind(self, inetSocketAddress)
  def receive = {
    case b @ Bound(localAddress) =>
    // do some logging or setup ...
    case CommandFailed(_: Bind) => {context stop self}
    case c @ Connected(remote, local) => {
      val handler = context.actorOf(Props[SimplisticHandler])
      val connection = sender()
      connection ! Register(handler)
    }
    case Tweet(tweet) =>{
      println(tweet)

  }
    case UpdatedUser(user) =>{
      println(user)
  }

  }
}