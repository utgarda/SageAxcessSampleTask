package com.sageaxces.sampletask.akka

import akka.actor.{Actor, Status}
import com.sageaxcess.sampletask.akka.LinesTokenizer

/**
  * Actor definitions for SageAxcess sample task implementation with Akka
  */
object Actors {

  case class ReadFile(name: String)

  case object EOF

  final case class InvalidFileException(name: String) extends RuntimeException(s"invalid file: $name")

  /**
    * Accepts tasks to read files, reads the header,
    * sends the rest of lines in separate messages to tokenizer actor
    * @param tokenizerPath tokenizer actor path in system, default is a sibling lookup path
    */
  class LinesReader(
                       tokenizerPath: String = s"../${LinesTokenizer.DefaultRelativePath}"
                     )
    extends Actor
    with akka.actor.ActorLogging {

    private def readFile(name: String): Unit = {
      log.info(s"reading file: $name")
      val lines = io.Source.fromFile(name).getLines()
      if (!lines.hasNext) {
        log.error(s"input file $name has no header line")
        throw InvalidFileException(name)
      } else {
        val header = lines.next()
        log.info(s"processign CSV with header: $header")
        sendLines(lines)
      }
    }

    private def sendLines(lines: Iterator[String]): Unit = {
      lines.foreach { l =>
        log.debug(s"line read: $l")
        println(l)
        context.actorSelection(tokenizerPath) ! l
      }
      log.debug("end of file reached")
      context.actorSelection(tokenizerPath) ! EOF
    }

    override def receive = {
      case ReadFile(name) ⇒
        log.info(s"accepted task to read file: $name")
        try {
          readFile(name)
        } catch {
          case e: Exception =>
            sender ! Status.Failure(e)
        }
    }
  }

}
