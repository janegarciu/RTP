import scala.collection.mutable.ListBuffer

case class Work(name:String)
case class ErrorMessage(error: String, actorAddress: String)
case class ActorPool(value:ListBuffer[String])
class RestartMeException extends Exception("RESTART")
class ResumeMeException extends Exception("RESUME")
