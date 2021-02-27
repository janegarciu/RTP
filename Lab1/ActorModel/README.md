#Table of contents
---------------------

 * [INTRODUCTION](#INTRODUCTION)
 * [TECHNOLOGIES](#TECHNOLOGIES)
 * [REQUIREMENTS](#REQUIREMENTS)
 * [IMPLEMENTATION](#IMPLEMENTATION)
     * [Connector](#Connector)
     * [AutoScaler](#AutoScaler)
     * [Router](#Router)
     * [Worker](#Worker)
     * [WorkerSupervisor](#WorkerSupervisor)
     * [WorkerProtocol](#WorkerProtocol)
     
 * [DEMO](#DEMO)
 
 INTRODUCTION
 ------------
 Scala based project implementing Actor System Model using akka-actor-typed-- library. This project is aimed to develop
 a good comprehension of Scala language and Akka framework as well as understanding of actor's concurrency capabilities.
 
 TECHNOLOGIES
 ------------
 Scala language version "2.13.4" and Akka framework version "2.5.31" were used for the project development.
 
 REQUIREMENTS
 ------------
 We had to process tweets that were coming from the stream after request on defined endpoints(tweets/1,/tweets/2).
 After that we had to compute the sentiment score of each tweet having emotion values defined on this endpoint(/emotion_values).
 We should have workers and a supervisor. We had to dynamically change the number of workers depending on the load of messages per second. 
 In case we receive a "panic" message we had to kill the worker actor and restart it. Workers also need to have a random sleep, in the range of 50ms to 500ms.
 Other additional tasks were described in email:
 * Speculative execution of slow tasks, for some details check the recording of the first lesson
 * "Least connected" load balancing, or even more advanced, check for some examples (not directly related): https://blog.envoyproxy.io/examining-load-balancing-algorithms-with-envoy-1be643ea121c
 * Have a metrics endpoint to monitor the stats on ingested messages, average execution time, 75th, 90th, 95th percentile execution time, number of crashes per given time window, etc
 * Anything else, like the most popular hashtag up until now, or maybe other analytics
 
 IMPLEMENTATION
 ------------
  ##Connector
  ------------
 
  _**Connector**_ Actor is an actor which performs connection to the defined in the requirements endpoints from which receive a stream of tweets we should analyze.
  It is initialized in object _**Main**_. Connector Actor is sending those received tweets to _**AutoScaler**_ actor and to _**Router**_ actor for further processing.

         router ! Work(resp.utf8String)
         autoScaler ! Work(resp.utf8String)
         
  ##AutoScaler
  ------------
  
  _**AutoScaler**_ Actor receives messages from _**Connector**_ and calculates the number of messages within a second using timer() method.
         
         
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
   
   
   Then it sends this number to _**Supervisor**_
  Actor which we will describe later in this README. 
  
  ##Router
  ------------
  
  _**Router**_ Actor receives tweets from _**Connector**_ Actor and its main role is to route those tweets to workers. 
  Workers are created dynamically in _**WorkerSupervisor**_ Actor. Router is using Round Robin strategy for a group of workers
  defined in _**RoundRobinRoutingLogic**_ object. Round Robin is sending so called "work" to workers in a specific order, concurrently to 
  the number of workers we have defined, from first worker to the last one, then repeating this logic.
  
         def RoundRobinLogic(list: ListBuffer[String]) = {
            currentIndex = currentIndex + 1
            if (currentIndex == list.length) {
              currentIndex = 0
            }
       
  ##Worker
  ------------
  
  _**Worker**_ Actor receives a message(tweet) from router and its main function is to analyze this tweet on emotion values and
  count the final value having those values for each emotion defined in _*EmotionValues.txt* file. I am using this pattern
  to get text value from the tweet we receive:
       
        val pattern2 = new Regex("\"(text)\":(\"((\\\\\"|[^\"])*)\"|)")
       
  Also, if message that we receive has the following pattern in it:
  
        val pattern = new Regex(": panic")
        
   we are restarting current worker using OneForOne Strategy defined in _**WorkerSupervisor**_ by throwing exception:
        
        throw new RestartMeException
   
  You can see it on the screenshot below:
  
  
  ![](/ActorModel/Screenshots/Exception.png)
  
   
  ##WorkerSupervisor
  ------------
  
  _**WorkerSupervisor**_ Actor is receiving the number of messages per second from _**AutoScaler**_ Actor and is dynamicaly updating the list of workers
  in dependence of the number received and is sending this updated list of workers back to the router.
  
            if (actorListLenght == maxNumberOfWorkers) {
              actorList.remove(actorListLenght - 1)
              actorListLenght = actorListLenght - 1
            }
            else if (messagesCount > 100) {
              val myWorkingActor = context.actorOf(Props[Worker](), name = "worker" + (actorListLenght + 1))
              actorListLenght = actorListLenght + 1
              actorList.addOne(myWorkingActor.path.toString)
            }
            router ! ActorPool(actorList)
          }
  
  ##WorkerProtocol
  ------------
  
  _**WorkerProtocol**_ class defines all case classes needed for the project as well as exceptions used by OneForOne supervisor strategy.
  
  
 DEMO
 ------------
 Link to output results of calculated emotion values:
 
 https://drive.google.com/file/d/1gVIMbsa9TA43Yemyx3yNxw3aR-7iwUA5/view?usp=sharing
 
 
 
 
  
 
 