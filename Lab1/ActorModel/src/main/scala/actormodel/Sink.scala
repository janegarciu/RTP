package actormodel
import java.util.{Timer, TimerTask}

import actormodel.Helpers.GenericObservable
import akka.actor.{Actor, ActorSelection}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import org.json4s.JsonDSL._
import org.json4s.{DefaultFormats, _}
import org.json4s.jackson.JsonMethods._
import org.mongodb.scala._

import scala.collection.mutable.ListBuffer

class Sink extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val uri: String = "mongodb+srv://garciuj:Password1@rtpcluster.lzv0q.mongodb.net/TweetsDB?retryWrites=true&w=majority"
  System.setProperty("org.mongodb.async.type", "netty")
  val client: MongoClient = MongoClient(uri)
  val db: MongoDatabase = client.getDatabase("TweetsDB")
  var tweetsDataCol: MongoCollection[Document] = db.getCollection("TweetsData")
  var usersDataCol: MongoCollection[Document] = db.getCollection("UsersData")
  var tweetBuffer = new ListBuffer[Document]()
  var userBuffer = new ListBuffer[Document]()
  var clientReceive: ActorSelection = context.system.actorSelection("user/clientReceive")

  var user: JObject = _
  implicit val formats = DefaultFormats
  timer()

  override def receive: Receive = {
    case NewTweet(newTweet, uuid) => {
      if ((newTweet \ "user") != JNothing) {
        user = (newTweet \ "user").extract[JObject]
      }
      else {
        user = (newTweet \ "message" \ "tweet" \ "user").extract[JObject]
      }
      val updatedUser = user ~ ("_id" -> uuid)
      clientReceive ! UpdatedUser(updatedUser.toString)
      clientReceive ! Tweet(newTweet.toString)
      val userDocument = Document(compact(render(updatedUser)))
      val tweetDocument = Document(compact(render(newTweet)))

      if (tweetBuffer.length == 20) {
        tweetsDataCol.insertMany(tweetBuffer.toList).results()
        usersDataCol.insertMany(userBuffer.toList).results()
        userBuffer.clear()
        tweetBuffer.clear()
      }
      else {
        tweetBuffer += tweetDocument
        userBuffer += userDocument
      }
    }
    case timerMessage: Boolean => {
      if (timerMessage) {
        timer()
      }
    }
  }

  def timer(): Unit = {
    val trigger = new Timer()
    trigger.scheduleAtFixedRate(new TimerTask {
      def run() = {
        if (tweetBuffer.length != 20 && tweetBuffer.nonEmpty && userBuffer.nonEmpty) {
          tweetsDataCol.insertMany(tweetBuffer.toList).results()
          usersDataCol.insertMany(userBuffer.toList).results()
          tweetBuffer.clear()
          userBuffer.clear()
          self ! true
        }
        trigger.cancel()
      }
    }, 200, 1)
  }
}
