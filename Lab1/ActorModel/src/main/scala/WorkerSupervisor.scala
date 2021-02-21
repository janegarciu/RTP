import WorkerSupervisor.actorList
import akka.actor.{Actor, ActorSelection, Props}

object WorkerSupervisor {
  var actorList: List[String] = List[String]()
}

class WorkerSupervisor extends Actor {
  var router : ActorSelection = context.system.actorSelection("user/router")

  override def preStart(): Unit = {
    for (a <- 1 to 5){
      val myWorkingActor = context.actorOf(Props[Worker](),name = "worker" + a)
      actorList :+= myWorkingActor.path.toString
    }
    router.!(ActorPoll(actorList))(self)
  }


  override def receive: Receive = {
    case _ =>
  }
}
