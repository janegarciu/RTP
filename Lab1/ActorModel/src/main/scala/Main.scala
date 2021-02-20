import akka.actor.TypedActor.context
import akka.actor.{ActorSystem, Props}
import akka.stream.ActorMaterializer


object Main extends App {
  println("Entry point!")
  //implicit val system: ActorSystem = ActorSystem()



  val system = ActorSystem("MyMagicSystem")

  /*val router = system.actorOf(Props[RouterActor], name = "router")
   val connectionActor = system.actorOf(Props(new ConnectionActor(router)),  name = "connectionActor")*/

  val connector = system.actorOf(Props [Connector], name = "connectionActor")



}