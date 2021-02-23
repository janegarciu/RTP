
import akka.actor.{Actor, ActorLogging, ActorSelection}

import scala.io.Source
import scala.util.matching.Regex

class Worker extends Actor with ActorLogging {
  var workerSupervisor : ActorSelection = context.system.actorSelection("user/supervisor")
  val mapData = readTextFile("/Users/janegarciu/Documents/RTP/Lab1/ActorModel/src/main/scala/EmotionValues.txt")

  def countEmotionValuesOfTweets(wordList: List[String]): Unit ={
    var counter = 0
    wordList.foreach(word => {
      if(mapData.contains(word)) {
        counter += mapData.get(word).head
      }
    })
    println("Emotion value of a tweet:" + counter)
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

  override def receive={
    case Work(msg) => {
      val pattern = new Regex(": panic")
      val pattern2 = new Regex("\"(text)\":(\"((\\\\\"|[^\"])*)\"|)")

      val parsedTweet = pattern findFirstIn  msg
      if(parsedTweet.isDefined){
        workerSupervisor.! (ErrorMessage(parsedTweet.toString, self.path.toString))(self)
      }
      else {
        val tweetText = pattern2 findFirstIn  msg
        tweetText.map(myString =>{
          println("Text of a tweet:" + myString)
          countEmotionValuesOfTweets(myString.split("\":\"")(1).split(" ").toList)
        })
      }
    }
  }
}

