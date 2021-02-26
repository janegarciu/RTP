import scala.collection.mutable.ListBuffer

case class Work(name: String)

case class ErrorMessage(error: String, actorAddress: String)

case class ActorPool(value: ListBuffer[String])

case class Sleep(duration: Any)

class RestartMeException extends Exception("RESTART")

class ResumeMeException extends Exception("RESUME")
