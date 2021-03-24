import akka.actor.{ActorSystem, Props}


object Main extends App {

  val system = ActorSystem("myMagicSystem")
  val router = system.actorOf(Props[Router], "router")
  val connector = system.actorOf(Props[Connector], name = "connectionActor")
  val workerSupervisor = system.actorOf(Props[WorkerSupervisor], name = "supervisor")
  val workerSupervisor2 = system.actorOf(Props[WorkerSupervisor2], name = "supervisor2")
  val autoScaler = system.actorOf(Props[AutoScaler], name = "autoScaler")
  val aggregator = system.actorOf(Props[Aggregator], name = "aggregator")


}