package com.sageaxcess.sampletask.akka

import akka.actor.SupervisorStrategy.Escalate
import akka.actor._
import com.sageaxcess.sampletask.akka.Actors._

case object Init

/**
  * Starts 3 actors for leading lines, processing them, and counting tokens
  */
class Supervisor(outputFileName: String) extends Actor with akka.actor.ActorLogging {
  val linesReader = context.actorOf(Props(new LinesReader()))
  val linesTokenizer = context.actorOf(Props(new LinesTokenizer()), LinesTokenizerDefaultPath)
  val tokensCounter = context.actorOf(Props(new TokensCounter(outputFileName)), TokensCounterDefaultPath)

  override val supervisorStrategy =
    AllForOneStrategy() {
      case _ => Escalate
    }

  override def receive: Receive = {
    case rf: ReadFile =>
      log.debug(s"forwarding message: $rf")
      linesReader forward rf
    case f: Status.Failure =>
      log.error("Like, totally critical failure", f)
      context.system.terminate()
    case Done =>
      log.info("All done!")
      context.system.terminate()
  }
}
