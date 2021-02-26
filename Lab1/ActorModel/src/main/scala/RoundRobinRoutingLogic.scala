import scala.collection.mutable.ListBuffer

object RoundRobinRoutingLogic {
  var currentIndex = 0

  def RoundRobinLogic(list: ListBuffer[String]) = {
    currentIndex = currentIndex + 1
    if (currentIndex == list.length) {
      currentIndex = 0
    }
  }
}

