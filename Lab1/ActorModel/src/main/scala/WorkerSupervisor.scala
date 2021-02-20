import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import WorkerProtocol.Work

class WorkerSupervisor extends Actor{
  val system = ActorSystem("workerSupervisor")
  system.actorOf(Props[Worker],name = "worker1")
  system.actorOf(Props[Worker],name = "worker2")
  system.actorOf(Props[Worker],name = "worker3")

  val workers :List[String]=List("/user/worker1",
    "/user/worker2",
    "/user/worker3")

  override def receive: Receive = {
    case msg:String =>
      val router = system.actorOf(Props(classOf[Router], workers))
      router ! Work(msg)
  }
}
