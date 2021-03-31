import akka.actor.{Actor, ActorSelection}
import org.json4s.JObject
import org.json4s.JsonDSL._

class Aggregator extends Actor {
  var sink: ActorSelection = context.system.actorSelection("user/sink")
  var tweetMap: Map[String, JObject] = Map()
  var engagementRatioMap: Map[String, Int] = Map()
  var emotionValueMap: Map[String, Int] = Map()
  var newTweet: JObject = _

  override def receive: Receive = {
    case JsonWrapper(tweet, uuid) => {
      tweetMap += (uuid -> tweet)
      if (engagementRatioMap.contains(uuid) && emotionValueMap.contains(uuid)) {
        newTweet = tweet ~ ("engagement_ratio" -> engagementRatioMap(uuid)) ~ ("emotion_value" -> emotionValueMap(uuid)) ~ ("user_id" -> uuid)
        sink.!(NewTweet(newTweet, uuid))(self)
      }
      else {
        self.!(JsonWrapper(tweetMap(uuid), uuid))(self)
      }
    }
    case EngagementRatio(engagementRatioValue, uuid) => {
      engagementRatioMap += (uuid -> engagementRatioValue)

    }
    case EmotionValue(emotionValue, uuid) => {
      emotionValueMap += (uuid -> emotionValue)
    }
  }
}
