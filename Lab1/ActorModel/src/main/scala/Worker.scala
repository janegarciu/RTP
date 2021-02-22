
import akka.actor.{Actor, ActorLogging}

import scala.io.Source
import scala.util.matching.Regex

class Worker extends Actor with ActorLogging {

  def readTextFile(filename: String) = {
    val pairs =
      for {
        line <- Source.fromFile(filename).getLines()
        split = line.split("\t").map(_.trim).toList
        name = split.head
        scores = split.tail.map(_.toInt)
      } yield (name -> scores)
    pairs.toMap
  }
  val mapData = readTextFile("/Users/janegarciu/Documents/RTP/Lab1/ActorModel/src/main/scala/EmotionValues.txt")

  override def receive={
    case Work(msg) => {
      val pattern = new Regex("\"(text)\":(\"((\\\\\"|[^\"])*)\"|)")
      val parsedText = pattern findFirstIn  msg
      parsedText.map(myString =>
        myString.split("\":\"")(1).split(" ").toList.foreach(a => {
          var counter = 0
          if(mapData.contains(a)){
            counter+=mapData.get(a).head.head
            println(s"Tweet emotion equals to:" + a )
            println(s"Tweet score equals to:" + counter)
          }
        }))
      //or regex: myString.findFirstIn(value).map(a=>println(a.substring(a.indexOf(":")+1)))

      //log.info(s"I received work message $msg and my actorRef:${self.path.name}")
    }
  }
}

