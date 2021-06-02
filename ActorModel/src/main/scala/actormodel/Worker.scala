package actormodel
import akka.actor.{Actor, ActorLogging, ActorSelection}
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.io.Source
import scala.util.Random
import scala.util.matching.Regex

class Worker extends Actor with ActorLogging {
  val mapData = readTextFile("/Users/janegarciu/Documents/RTP/Lab1/ActorModel/src/main/scala/actormodel/EmotionValues.txt")
  var workerSupervisor: ActorSelection = context.system.actorSelection("user/supervisor")
  var aggregator: ActorSelection = context.system.actorSelection("user/aggregator")

  implicit val formats = DefaultFormats
  var text: String = new String()

  override def preRestart(reason: Throwable, message: Option[Any]) = {
    println("Restarting...")
    super.preRestart(reason, message)
  }

  override def postRestart(reason: Throwable) = {
    println("...restart completed!")
    super.postRestart(reason)
  }

  def readTextFile(filename: String) = {

    val pairs =
      for {
        line <- Source.fromFile(filename).getLines()
        split = line.split("\t").map(_.trim).toList
        name = split.head
        scores = split.tail.map(_.toInt).last
      } yield (name -> scores)
    pairs.toMap
  }

  override def receive: Receive = {
    case Work(msg, uuid) => {
      Thread.sleep(Random.nextInt(450) + 50)
      val pattern = new Regex(": panic")
      val parsedTweet = pattern findFirstIn msg
      if (parsedTweet.isDefined) {
        throw new RestartMeException
      }
      else {
        val json = parse(msg)
        if ((json \ "text") != JNothing) {
          text = (json \ "text").extract[String]

        }
        else {
          text = (json \ "message" \ "tweet" \ "text").extract[String]
        }
        val deserializedMessage: Array[String] = text.split(" ")
        val emotionValue: Int = countEmotionValuesOfTweets(deserializedMessage)
        aggregator.!(EmotionValue(emotionValue, uuid))(self)
        aggregator.!(JsonWrapper(json.asInstanceOf[JObject], uuid))(self)
      }
    }
  }

  def countEmotionValuesOfTweets(wordList: Array[String]): Int = {
    var counter = 0
    wordList.foreach(word => {
      if (mapData.contains(word)) {
        counter += mapData.get(word).head
      }
    })
    log.info("Emotion value of a tweet:" + counter)
    counter
  }
}

