import akka.actor.Actor

class Aggregator extends Actor{
  var tweetMap: Map[String, Any] = Map()
  override def receive: Receive = {
    case JsonWrapper(tweet, uuid) => {
      tweetMap += (uuid -> tweet)

    }
    case EngagementRatio(engagementRatioValue, uuid) => {
      if(tweetMap != 0 && tweetMap.contains(uuid)){
        println("Tweetmap key:"+ tweetMap.get(uuid))
      }
    }
    case EmotionValue(emotionValue, uuid) => {
      if(tweetMap != 0 && tweetMap.contains(uuid)){
        println("Tweetmap key:"+ tweetMap.get(uuid))
      }
    }
  }
}
