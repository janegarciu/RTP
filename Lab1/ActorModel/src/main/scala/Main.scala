import akka.actor.{ActorSystem, Props}


object Main extends App {

  val system = ActorSystem("myMagicSystem")
  val router = system.actorOf(Props[Router], "router")
  val connector = system.actorOf(Props[Connector], name = "connectionActor")
  val workerSupervisor = system.actorOf(Props[WorkerSupervisor], name = "supervisor")
  val autoScaler = system.actorOf(Props[AutoScaler], name = "autoScaler")

}