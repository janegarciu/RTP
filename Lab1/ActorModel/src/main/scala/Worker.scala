
import akka.actor.{Actor, ActorLogging, ActorSelection}

import scala.io.Source
import scala.util.Random
import scala.util.matching.Regex

class Worker extends Actor with ActorLogging{
  val mapData = readTextFile("/Users/janegarciu/Documents/RTP/Lab1/ActorModel/src/main/scala/EmotionValues.txt")
  var workerSupervisor: ActorSelection = context.system.actorSelection("user/supervisor")

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

  override def receive = {
    case Work(msg) => {
      Thread.sleep(Random.nextInt(450) + 50)

      val pattern = new Regex(": panic")
      val pattern2 = new Regex("\"(text)\":(\"((\\\\\"|[^\"])*)\"|)")

      val parsedTweet = pattern findFirstIn msg
      if (parsedTweet.isDefined) {
        //println("---------" + self.path.toString)
        throw new RestartMeException
      }
      else {
        val tweetText = pattern2 findFirstIn msg
        tweetText.map(myString => {
          //println("Text of a tweet:" + myString)
          countEmotionValuesOfTweets(myString.split("\":\"")(1).split(" ").toList)
        })
      }
    }
  }

  def countEmotionValuesOfTweets(wordList: List[String]): Unit = {
    var counter = 0
    wordList.foreach(word => {
      if (mapData.contains(word)) {
        counter += mapData.get(word).head
      }
    })
    log.info("Emotion value of a tweet:" + counter)
  }
}

