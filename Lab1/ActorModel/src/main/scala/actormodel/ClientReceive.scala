package actormodel

import java.net.InetSocketAddress

import akka.actor.{Actor}
import akka.io.{IO, Tcp}

class ClientReceive(remote: InetSocketAddress) extends Actor{
  import Tcp._
  import context.system
  IO(Tcp) ! Connect(remote)
  override def receive: Receive= {
    case Tweet(tweet) =>{
      IO(Tcp) ! Tweet(tweet)
    }

    case UpdatedUser(user) => {
      IO(Tcp) ! UpdatedUser(user)
    }

  }

}
