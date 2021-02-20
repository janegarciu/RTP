
import akka.actor.{Actor, ActorLogging}
import WorkerProtocol.Work

class Worker extends Actor with ActorLogging {
  override def receive={
    case Work(msg) => log.info(s"I received work message $msg and my actorRef:${self.path.name}")
  }
}
