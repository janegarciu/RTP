import akka.actor.{Actor, ActorLogging, ActorSelection}
import scala.io.Source
import scala.util.Random
import scala.util.matching.Regex

class Worker extends Actor with ActorLogging {
  val mapData = readTextFile("/Users/janegarciu/Documents/RTP/Lab1/ActorModel/src/main/scala/EmotionValues.txt")
  var workerSupervisor: ActorSelection = context.system.actorSelection("user/supervisor")
  var aggregator: ActorSelection = context.system.actorSelection("user/aggregator")

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
    case Work(msg, uuid) => {
      Thread.sleep(Random.nextInt(450) + 50)
      val json : String = ujson.read(msg)("message")("tweet")("text").value.toString
      val tweet : Any = ujson.read(msg)("message")("tweet").value
      aggregator.! (JsonWrapper(tweet, uuid))(self)

      val deserializedMessage: Array[String] = json.split(" ")
      val pattern = new Regex(": panic")
      val parsedTweet = pattern findFirstIn msg

      if (parsedTweet.isDefined) {
        //println("---------" + self.path.toString)
        throw new RestartMeException
      }
      else {
        //println("Text of a tweet:" + myString)
        val emotionValue: Int = countEmotionValuesOfTweets(deserializedMessage)
        aggregator.! (EmotionValue(emotionValue, uuid))(self)
        }
      }
    }

  //    def runCommand() {
  //      val command = Seq("docker", "restart", "46a88e10abbd")
  //      val os = sys.props("os.name").toLowerCase
  //      val panderToWindows = os match {
  //        case x if x contains "windows" => Seq("cmd", "/C") ++ command
  //        case _ => command
  //      }
  //      panderToWindows.!
  //    }

  def countEmotionValuesOfTweets(wordList: Array[String]): Int = {
    var counter = 0
    wordList.foreach(word => {
      if (mapData.contains(word)) {
        counter += mapData.get(word).head
      }
    })
    log.info("Emotion value of a tweet:" + counter)
    return counter
  }
}

