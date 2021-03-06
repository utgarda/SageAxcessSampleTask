package com.sageaxcess.sampletask.akka

import java.io.{File, PrintWriter}

import akka.actor.{Actor, Status}
import com.sageaxcess.sampletask.tokenizer.Tokenizer

import scala.collection.mutable

/**
  * Actor definitions for SageAxcess sample task implementation with Akka
  */
object Actors {

  case class ReadFile(name: String)

  case object EOF

  final case class InvalidFileException(name: String) extends RuntimeException(s"invalid file: $name")

  case object Done

  /**
    * Accepts tasks to read files, reads the header,
    * sends the rest of lines in separate messages to tokenizer actor
    * @param tokenizerPath tokenizer actor path in system, default is a sibling lookup path
    */
  class LinesReader(
                     tokenizerPath: String = s"../$LinesTokenizerDefaultPath"
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

  case class ChangeSeparator(c: Char)

  /**
    * Accpts lines for processing, sends results to tokens counter,
    * accepts custom separators in messages like <code>ChangeSeparator('|')</code>
    * @param counterPath counter actor path in system, default is a sibling lookup path
    */
  class LinesTokenizer(counterPath: String = s"../$TokensCounterDefaultPath")
    extends Actor
    with akka.actor.ActorLogging {

    val DEFAULT_SEPARATOR = ','

    var separator = DEFAULT_SEPARATOR

    private def tokenizeLine(line: String): Unit = {
      log.debug(s"processing line: $line")
      Tokenizer.tokenize(separator)(line).foreach { token =>
        log.debug(s"sending token: $token")
        context.actorSelection(counterPath) ! token
      }
    }

    override def receive: Receive = {
      case ChangeSeparator(newSeparator: Char) =>
        log.debug(s"changing separator to $newSeparator")
        separator = newSeparator
      case line: String => tokenizeLine(line)
      case EOF =>
        context.actorSelection(counterPath) ! EOF
    }
  }

  /**
    * Accepts tokens, counts them, writes to file on EOF,
    */
  class TokensCounter(
                       outputFileName: String,
                       outputSeparator: String = ":"
                     )
    extends Actor
    with akka.actor.ActorLogging {

    val counts: mutable.Map[String, Long] = new mutable.HashMap[String, Long]

    private def writeResult(): Unit = {
      log.debug(s"writing result token counts to $outputFileName")
      val writer = new PrintWriter(new File(outputFileName))
      counts.foreach {
        case (token: String, count: Long) =>
          writer.print(token)
          writer.print(outputSeparator)
          writer.println(count)
      }
      writer.flush()
      writer.close()
      context.parent ! Done
    }

    override def receive: Actor.Receive = {
      case token: String =>
        counts.put(token, counts.getOrElse(token, 0L) + 1)
        log.debug(s"new count for token $token is ${counts(token)}")
      case EOF =>
        try {
          writeResult()
        } catch {
          case e: Exception =>
            log.error("can't write results to file", e)
            context.parent ! Status.Failure(e)
        }
    }
  }

  val LinesTokenizerDefaultPath = "lines-tokenizer"
  val TokensCounterDefaultPath = "tokens-counter"
}
