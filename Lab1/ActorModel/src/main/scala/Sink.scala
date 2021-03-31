import java.util.{Timer, TimerTask}
import Helpers.GenericObservable
import akka.actor.Actor
import org.json4s.DefaultFormats
import org.json4s.JsonAST.JObject
import org.json4s.jackson.JsonMethods._
import org.json4s.JsonDSL._
import org.mongodb.scala._
import scala.collection.mutable.ListBuffer

class Sink extends Actor {
  val uri: String = "mongodb+srv://garciuj:Password1@rtpcluster.lzv0q.mongodb.net/TweetsDB?retryWrites=true&w=majority"
  System.setProperty("org.mongodb.async.type", "netty")
  val client: MongoClient = MongoClient(uri)
  val db: MongoDatabase = client.getDatabase("TweetsDB")
  var tweetsDataCol: MongoCollection[Document] = db.getCollection("TweetsData")
  var usersDataCol: MongoCollection[Document] = db.getCollection("UsersData")
  tweetsDataCol.drop()
  usersDataCol.drop()
  var tweetBuffer = new ListBuffer[Document]()
  var userBuffer = new ListBuffer[Document]()
  implicit val formats = DefaultFormats
  timer()

  override def receive: Receive = {
    case NewTweet(newTweet, uuid) => {
      val user: JObject = (newTweet \ "message" \ "tweet" \ "user").extract[JObject]
      val updatedUser = user ~ ("_id" -> uuid)
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
        println("Buffer length:" + tweetBuffer.length)
        self ! true
        if(tweetBuffer.length != 20) {
          tweetsDataCol.insertMany(tweetBuffer.toList).results()
          usersDataCol.insertMany(userBuffer.toList).results()
          tweetBuffer.clear()
        }
        trigger.cancel()
      }
    }, 200, 1)
  }
}
