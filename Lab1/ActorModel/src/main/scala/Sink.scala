import akka.actor.Actor
import org.mongodb.scala._

import scala.collection.mutable.Queue

class Sink extends Actor {
  //  val uri: String = "mongodb+srv://<username>:<password>@<cluster-address>/test?retryWrites=true&w=majority"
  //  System.setProperty("org.mongodb.async.type", "netty")
  //  val client: MongoClient = MongoClient(uri)
  //  val db: MongoDatabase = client.getDatabase("test")
  var queue = new Queue[100]

  override def receive: Receive = {
    case NewTweet(newTweet) => {
      println("New tweet with new fields:" + newTweet)
    }
  }
}
