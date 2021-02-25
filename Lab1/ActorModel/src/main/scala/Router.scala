import akka.actor.{Actor, ActorLogging}
import RoundRobinRoutingLogic.{RoundRobinLogic, currentIndex}

import scala.collection.mutable.ListBuffer

class Router extends Actor with ActorLogging{

  var myActorAddress: ListBuffer[String] = _

  override def receive: Receive = {
    case ActorPool(listBuffer)=>
      myActorAddress = listBuffer
    case Work(msg) =>
      RoundRobinLogic(myActorAddress)
      context.system.actorSelection(myActorAddress(currentIndex)) forward Work(msg)
  }

}