package actormodel

import java.net.InetSocketAddress

import akka.actor.{ActorSystem, Props}

object Main extends App {

  val host = "localhost"
  val port = 9900

  val system = ActorSystem("myMagicSystem")
  val router = system.actorOf(Props[Router], "router")
  val connector = system.actorOf(Props[Connector], name = "connectionActor")
  val workerSupervisor = system.actorOf(Props[WorkerSupervisor], name = "supervisor")
  val workerSupervisor2 = system.actorOf(Props[WorkerSupervisor2], name = "supervisor2")
  val autoScaler = system.actorOf(Props[AutoScaler], name = "autoScaler")
  val aggregator = system.actorOf(Props[Aggregator], name = "aggregator")
  val sink = system.actorOf(Props[Sink], name = "sink")

  val clientActorModelProps = ClientActorModel.props(new InetSocketAddress(host, port), null)
  val clientActorModel = system.actorOf(clientActorModelProps)

  val serverBuffer = system.actorOf(Props(new ServerBuffer(clientActorModel)), "serverBuffer")


}
