package actormodel

import WorkerSupervisor.actorList
import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorSelection, OneForOneStrategy, Props}

import scala.collection.mutable.ListBuffer

class WorkerSupervisor extends Actor {
  var router: ActorSelection = context.system.actorSelection("user/router")
  var actorListLenght: Int = 0
  var maxNumberOfWorkers = 50

  override def preStart(): Unit = {
    for (a <- 1 to 5) {
      val myWorkingActor = context.actorOf(Props[Worker](), name = "worker" + a)
      actorList :+= myWorkingActor.path.toString
    }
    actorListLenght = actorList.length
    router.!(ActorPool(actorList))(self)
  }

  override def supervisorStrategy = OneForOneStrategy() {
    case _: RestartMeException => Restart
  }

  override def receive: Receive = {
    case messagesCount: Int => {
      //println("Number of messages" + messagesCount)
      if (actorListLenght == maxNumberOfWorkers) {
        actorList.remove(actorListLenght - 1)
        actorListLenght = actorListLenght - 1
      }
      else if (messagesCount > 100) {
        val myWorkingActor = context.actorOf(Props[Worker](), name = "worker" + (actorListLenght + 1))
        actorListLenght = actorListLenght + 1
        actorList.addOne(myWorkingActor.path.toString)
      }
      router ! ActorPool(actorList)
    }
  }
}
object WorkerSupervisor {
  var actorList: ListBuffer[String] = ListBuffer[String]()
}
