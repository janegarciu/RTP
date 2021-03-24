import akka.actor.{Actor, ActorSelection}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import java.util.UUID.randomUUID

import scala.util.matching.Regex

class Connector extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  var router: ActorSelection = context.system.actorSelection("user/router")
  var autoScaler: ActorSelection = context.system.actorSelection("user/autoScaler")

  var pattern = new Regex("\\{(.*)\\}")

  override def preStart(): Unit = {
    List(HttpRequest(method = HttpMethods.GET, uri = "http://localhost:4000/tweets/1"), HttpRequest(method = HttpMethods.GET, uri = "http://localhost:4000/tweets/2"))
      .foreach(endpoint => {
        Http(context.system).singleRequest(endpoint).pipeTo(self)
      })

  }

  def receive: Receive = {
    case HttpResponse(StatusCodes.OK, _, entity, _) =>
        entity.withoutSizeLimit().dataBytes.runForeach(resp => {
        val uuid = randomUUID().toString
        val message = pattern findFirstIn resp.utf8String
        router ! Work(message.get, uuid)
        router ! Work2(message.get, uuid)
        autoScaler ! Message(resp.utf8String)
      })
    case resp@HttpResponse(code, _, _, _) =>
      println("Request failed, response code: " + code)
      resp.discardEntityBytes()
  }
}