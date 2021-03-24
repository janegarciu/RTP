import WorkerSupervisor2.actorList2
import akka.actor.{Actor, ActorSelection, Props}

import scala.collection.mutable.ListBuffer

class WorkerSupervisor2 extends Actor {
  var router: ActorSelection = context.system.actorSelection("user/router")

  override def preStart(): Unit = {

    for (a <- 1 to 5) {
      val myNewWorkingActor = context.actorOf(Props[Worker2](), name = "newWorker" + a)
      actorList2 :+= myNewWorkingActor.path.toString
    }
    router.!(ActorPool2(actorList2))(self)
  }

  override def receive: Receive = {
    case count =>

  }
}
  object WorkerSupervisor2 {
    var actorList2: ListBuffer[String] = ListBuffer[String]()
  }

