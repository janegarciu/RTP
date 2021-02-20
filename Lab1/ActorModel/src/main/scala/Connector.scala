
import Main.system
import akka.actor.{Actor, ActorRef, Props}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import WorkerProtocol.Work

class Connector extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  val supervisor = system.actorOf(Props [WorkerSupervisor], name = "workerSupervisor")

  override def preStart(): Unit = {
    List(HttpRequest(method = HttpMethods.GET,uri = "http://localhost:4000/tweets/1"),HttpRequest(method = HttpMethods.GET,uri = "http://localhost:4000/tweets/2"))
      .foreach(endpoint=>{
        Http(context.system).singleRequest(endpoint)
          .pipeTo(self)
      })
    /*Http(context.system).singleRequest(HttpRequest(method = HttpMethods.GET,uri = "http://localhost:4000/tweets/1"))
      .pipeTo(self)*/
  }

  def receive: Receive = {
    case HttpResponse(StatusCodes.OK, _, entity, _) =>
      entity.withoutSizeLimit().dataBytes.runForeach(resp => {
        supervisor ! resp.utf8String
      })
    case resp @ HttpResponse(code, _, _, _) =>
      println("Request failed, response code: " + code)
      resp.discardEntityBytes()
  }

}