import scala.collection.mutable.ListBuffer

class RoundRobinRoutingLogic()
{
  var index = 0
  def RoundRobinLogic(list: ListBuffer[String]) = {
    index = index + 1
    if (index == list.length) {
      index = 0
    }
  }
}

