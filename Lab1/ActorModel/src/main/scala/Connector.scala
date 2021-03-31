import java.util.UUID.randomUUID

import akka.actor.{Actor, ActorSelection}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpMethods, HttpRequest, HttpResponse, StatusCodes}
import akka.stream.{ActorMaterializer, ActorMaterializerSettings}
import org.json4s._
import org.json4s.jackson.JsonMethods._

import scala.util.matching.Regex

class Connector extends Actor {

  import akka.pattern.pipe
  import context.dispatcher

  final implicit val materializer: ActorMaterializer = ActorMaterializer(ActorMaterializerSettings(context.system))
  var router: ActorSelection = context.system.actorSelection("user/router")
  var autoScaler: ActorSelection = context.system.actorSelection("user/autoScaler")
  implicit val formats = DefaultFormats

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
        val pattern2 = new Regex(": panic")
        val parsedTweet = pattern2 findFirstIn message.get
        if (parsedTweet.isEmpty && (parse(message.get) \ "message" \ "tweet" \ "retweeted_status") != JNothing) {
          val retweet = (parse(message.get) \ "message" \ "tweet" \ "retweeted_status").extract[JObject]
          val uuid2 = randomUUID().toString
          router ! Work(compact(render(retweet)), uuid2)
          router ! Work2(compact(render(retweet)), uuid2)
        }
        router ! Work(message.get, uuid)
        router ! Work2(message.get, uuid)
        autoScaler ! Message(resp.utf8String)
      })
    case resp@HttpResponse(code, _, _, _) =>
      println("Request failed, response code: " + code)
      resp.discardEntityBytes()
  }

  implicit class JValueExtended(value: JValue) {
    def has(childString: String): Boolean = {
      if ((value \ childString) != JNothing) {
        true
      } else {
        false
      }
    }
  }

}