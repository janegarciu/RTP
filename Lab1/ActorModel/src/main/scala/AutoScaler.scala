import java.util.{Timer, TimerTask}

import akka.actor.{Actor, ActorSelection}

import scala.collection.mutable
import scala.language.postfixOps

class AutoScaler extends Actor {
  var queue = mutable.Queue[String]()
  var supervisor: ActorSelection = context.system.actorSelection("user/supervisor")
  var count = 0
  timer()

  def receive(): Receive = {
    case Message(msg) => {
      queue.addOne(msg)
    }
    case timerMessage: Boolean => {
      if (timerMessage) {
        timer()
      }
    }
  }

  def timer(): Unit = {
    val trigger = new Timer()
    trigger.scheduleAtFixedRate(new TimerTask {
      def run() = {
        supervisor ! queue.length
        queue.removeAll()
        self ! true
        trigger.cancel()
      }
    }, 1000, 1)
  }
}

