package tcp.server

import akka.actor.{Actor, ActorRef}
import akka.io.Tcp
import akka.util.ByteString

import scala.collection.mutable.ListBuffer

class SimplisticHandler extends Actor {

  import Tcp._


  var addressesTweets = new ListBuffer[ActorRef]
  var addressesUsers = new ListBuffer[ActorRef]

  def receive = {
    case Received(data) => {
//       val message = data.utf8String

      if (data.utf8String.contains("users_topic")) {
        println("Found users-topic")
        if (addressesUsers.nonEmpty) {
//          println("Sending users_topic to client...")
          addressesUsers.foreach(userSubscriber => {
            userSubscriber ! Write(ByteString("SERVER_Users_topics: ").concat(ByteString(data.utf8String)))
          })
        }
      } else if(data.utf8String.contains("tweets_topic")) {
        println("Found tweets-topic")
        if (addressesTweets.nonEmpty) {
//          println("Sending tweets_topic to client...")
          addressesTweets.foreach(tweetsSubscriber => {
            tweetsSubscriber ! Write(ByteString("SERVER_Tweets_topics: ").concat(ByteString(data.utf8String)))
          })
        }
      }
      else if(data.utf8String.contains("subscribe") && data.utf8String.contains("tweets")) {
        addressesTweets += sender()
      }
      else if(data.utf8String.contains("subscribe") && data.utf8String.contains("users")) {
        addressesUsers += sender()
      }
      else if(data.utf8String.contains("unsubscribe") && data.utf8String.contains("users")) {
        addressesUsers -= sender()
      }
      else if(data.utf8String.contains("unsubscribe") && data.utf8String.contains("tweets")) {
        addressesTweets -= sender()
      }
//      println("Sender Actor Path" + sender().path.toString)

    }

    //      println(s"Data received - ${data.utf8String}")
    //      sender() ! Write(ByteString("SERVER_RES: ").concat(data))
    case PeerClosed => context stop self
  }
}
