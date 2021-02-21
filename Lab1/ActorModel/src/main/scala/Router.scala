import akka.actor.{Actor, ActorLogging}
import RoundRobinRoutingLogic.{currentIndex, RoundRobinLogic}

class Router extends Actor with ActorLogging{

  var myActorAddress: List[String] = _

  override def receive: Receive = {
    case ActorPoll(msg)=>
      myActorAddress = msg
    case Work(msg) =>
      RoundRobinLogic(myActorAddress)
      context.system.actorSelection(myActorAddress(currentIndex)) forward Work(msg)
  }

}