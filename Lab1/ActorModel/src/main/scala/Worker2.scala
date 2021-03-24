import akka.actor.{Actor, ActorLogging, ActorSelection}

class Worker2 extends Actor with ActorLogging{
  var aggregator: ActorSelection = context.system.actorSelection("user/aggregator")

  override def receive: Receive = {
    case Work2(msg, uuid) => {
      if(msg != null) {
        val favourites: Int = ujson.read(msg)("message")("tweet")("user")("favourites_count").toString.toInt

        val followers: Int = ujson.read(msg)("message")("tweet")("user")("followers_count").toString.toInt

        val retweets: Int = ujson.read(msg)("message")("tweet")("retweet_count").toString.toInt

        val engagementRatio = countEngagementRatio(favourites, followers, retweets)

        aggregator.! (EngagementRatio(engagementRatio, uuid))(self)
      }
    }
  }

  def countEngagementRatio(favourites: Int, followers: Int, retweets: Int): Int ={
    var engagementRatio: Int = 0
    try{
      if(followers !=0) {
        engagementRatio = (favourites + retweets) / followers
        log.info("Engagement ratio  of a tweet:" + engagementRatio)
      }
      else{
        engagementRatio = 1
      }
    }
    catch{
      case e: Exception => println("Exception" + e)
    }
    return engagementRatio
  }
}
