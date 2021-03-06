package actormodel

import akka.util.ByteString
import org.json4s.JsonAST.JObject

import scala.collection.mutable.ListBuffer

case class Work(name: String,uuid: String)

case class Work2(name: String, uuid: String)

case class Message(name: String)

case class NewTweet(tweet: JObject, uuid: String)

case class JsonWrapper(json: JObject, uuid: String)

case class EngagementRatio(engagementRatioValue: Int, uuid: String)

case class EmotionValue(emotionValue: Int, uuid: String)

case class ErrorMessage(error: String, actorAddress: String)

case class ActorPool(value: ListBuffer[String])

case class ActorPool2(value: ListBuffer[String])

case class UpdatedUser(user: String)

case class Tweet(tweet: String)

class RestartMeException extends Exception("RESTART")

class ResumeMeException extends Exception("RESUME")

case class ClientTweet(user: ByteString)

