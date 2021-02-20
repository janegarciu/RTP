import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import akka.routing
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import WorkerProtocol.Work

class Router(routees : List[String]) extends Actor with ActorLogging{

    override def receive = {
      case msg: Work => log.info("I m a router and i recieved a message...")
        context.actorSelection(routees(util.Random.nextInt(routees.size))) forward (msg)
    }
}