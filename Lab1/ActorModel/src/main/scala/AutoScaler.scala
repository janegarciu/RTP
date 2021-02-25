import java.util.Calendar

import akka.actor.{Actor, ActorLogging, ActorSelection, Props}

import scala.collection.mutable;

class AutoScaler extends Actor{
  var queue = mutable.Queue[Long]()
  var supervisor : ActorSelection = context.system.actorSelection("user/supervisor")

  def receive(): Receive = {
    case timeStamp: Long =>
      queue.addOne(timeStamp)
      countMessages()

  }

  def countMessages(): Unit ={
    var messagesCount = 0
    var index = queue.length
     var currentElement = queue.last
    var isMatchToInterval = Calendar.getInstance().getTime.getTime - currentElement < 1000
    while ( index > 0  && isMatchToInterval ) {
      index -= 1
      currentElement = queue(index)
      messagesCount += 1
      isMatchToInterval = Calendar.getInstance().getTime.getTime - currentElement < 1000
    }
    supervisor ! messagesCount
  }
}