package actormodel


import akka.actor.{Actor, ActorRef}
import akka.util.ByteString

class ServerBuffer(clientActorModel: ActorRef) extends Actor{
  override def receive: Receive= {
    case Tweet(tweet) =>
      //println("Tweets in client receive:",tweet)
      clientActorModel ! ByteString(tweet)
    case UpdatedUser(user) =>
      //println("Users in client receive:",user)
      clientActorModel ! ByteString(user)

  }

}
