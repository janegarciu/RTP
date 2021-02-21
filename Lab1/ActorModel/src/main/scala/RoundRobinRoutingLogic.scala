object RoundRobinRoutingLogic {
  var currentIndex = 0
  def RoundRobinLogic(list: List[String]) = {
    currentIndex = currentIndex + 1
    if (currentIndex == list.length){
      currentIndex = 0
    }
  }
}

