package com.sageaxcess.sampletask.akka

import akka.actor.{ActorRef, ActorSystem, Props}
import com.sageaxcess.sampletask.akka.Actors.ReadFile

/**
  * Akka-based CSV files processing app,
  * example input file: <pre>
  * HeaderA,HeaderB,HeaderC
  * valueA1,valueB1,valueC1
  * valueA1,valueB2,value with spaces
  * </pre>
  * example output file: <pre>
  * valueA1:2
  * valueB1:1
  * valueC1:1
  * valueB2:1
  * value with spaces:1
  * </pre>
  * call via sbt:
  * sbt "akka/run input.csv output"
  */
object TokensCount extends App {
  private val DEFAULT_INPUT = "input.csv"
  private val DEFAULT_OUTPUT = "output"

  if (args.nonEmpty && Array("-h", "h", "--help", "help").contains(args(0))) {
    printHelpAndExit()
  }
  val inputFileName = if (args.nonEmpty) {
    args(0)
  } else DEFAULT_INPUT
  val outputFileName = if (args.length > 1) {
    args(1)
  } else DEFAULT_OUTPUT


  val system = ActorSystem("tokenscount")

  val supervisor = system.actorOf(Props(new Supervisor(outputFileName)))

  supervisor.tell(ReadFile(inputFileName), ActorRef.noSender)

  def printHelpAndExit(): Unit = {
    println(
      """Usage:
        |sbt "akka/run --help"
        |shows this message
        |
        |sbt "akka/run input.csv"
        |provide input file name, default is "input.csv"
        |
        |sbt "akka/run input.csv output"
        |provide both input and output file names, defaults are "input.csv", "output"
      """.stripMargin)
    System.exit(0)
  }
}
