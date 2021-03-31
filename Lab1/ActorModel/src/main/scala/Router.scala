import akka.actor.{Actor, ActorLogging}

import scala.collection.mutable.ListBuffer

class Router extends Actor with ActorLogging {

  var myActorAddress: ListBuffer[String] = _
  var newActorAddressList: ListBuffer[String] = _
  var roundRobinClass = new RoundRobinRoutingLogic()
  var roundRobinClass2 = new RoundRobinRoutingLogic()

  override def receive: Receive = {
    case ActorPool(listBuffer) =>
      myActorAddress = listBuffer
    case ActorPool2(listBuffer2) =>
      newActorAddressList = listBuffer2
    case Work(msg, uuid) => {
      roundRobinClass.RoundRobinLogic(myActorAddress)
      context.system.actorSelection(myActorAddress(roundRobinClass.index)) forward Work(msg, uuid)
    }
    case Work2(msg, uuid) => {
      roundRobinClass2.RoundRobinLogic(newActorAddressList)
      context.system.actorSelection(newActorAddressList(roundRobinClass2.index)) forward Work2(msg, uuid)
    }
  }
}