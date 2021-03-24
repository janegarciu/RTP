import ujson.Value

import scala.collection.mutable.ListBuffer

case class Work(name: String,uuid: String)

case class Work2(name: String, uuid: String)

case class Message(name: String)

case class JsonWrapper(json: Any, uuid: String)

case class EngagementRatio(engagementRatioValue: Int, uuid: String)

case class EmotionValue(emotionValue: Int, uuid: String)

case class ErrorMessage(error: String, actorAddress: String)

case class ActorPool(value: ListBuffer[String])

case class ActorPool2(value: ListBuffer[String])

class RestartMeException extends Exception("RESTART")

class ResumeMeException extends Exception("RESUME")
