package actormodel
import akka.actor.{Actor, ActorLogging, ActorSelection}
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.Random
import scala.util.matching.Regex


class Worker2 extends Actor with ActorLogging {
  var aggregator: ActorSelection = context.system.actorSelection("user/aggregator")

  implicit val formats = DefaultFormats
  var favourites: Int = 0
  var followers: Int = 0
  var retweets: Int = 0

  override def receive: Receive = {
    case Work2(msg, uuid) => {
      Thread.sleep(Random.nextInt(450) + 50)
      if (msg != 0) {
        val pattern = new Regex(": panic")
        val parsedTweet = pattern findFirstIn msg
        if (parsedTweet.isDefined) {
          throw new RestartMeException
        }
        else {
          val json = parse(msg)
          if ((json \ "user") != JNothing) {
            favourites = (json \ "user" \ "favourites_count").extract[Int]

            followers = (json \ "user" \ "followers_count").extract[Int]

            if ((json \ "retweet_count") != JNothing) {

              retweets = (json \ "retweet_count").extract[Int]
            }
            else {
              retweets = 0
            }

          }
          else {
            favourites = (json \ "message" \ "tweet" \ "user" \ "favourites_count").extract[Int]

            followers = (json \ "message" \ "tweet" \ "user" \ "followers_count").extract[Int]

            retweets = (json \ "message" \ "tweet" \ "retweet_count").extract[Int]
          }

          val engagementRatio = countEngagementRatio(favourites, followers, retweets)
          aggregator.!(EngagementRatio(engagementRatio, uuid))(self)
        }
      }
    }
  }

  def countEngagementRatio(favourites: Int, followers: Int, retweets: Int): Int = {
    var engagementRatio: Int = 0
    try {
      if (followers != 0) {
        engagementRatio = (favourites + retweets) / followers
        log.info("Engagement ratio  of a tweet:" + engagementRatio)
      }
      else {
        engagementRatio = 1
      }
    }
    catch {
      case e: Exception => println("Exception" + e)
    }
    engagementRatio
  }
}
