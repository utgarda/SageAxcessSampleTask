package com.sageaxcess.sampletask.plainscala

import java.io.{File, PrintWriter}

import com.sageaxcess.sampletask.tokenizer.Tokenizer

/**
  * CSV files processing app,
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
  * sbt "plainscala/run input.csv output"
  */
object TokensCount extends App {
  private val OUTPUT_SEPARATOR = ':'
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

  val lines = io.Source.fromFile(inputFileName).getLines()
  if (!lines.hasNext) {
    println("empty input file")
    System.exit(1)
  }
  val header = lines.next()
  val tokens = getTokens(lines)
  println(s"Processing CSV values with header: $header")

  val counts = getCounts(tokens)
  printOutputSample()
  println(s"Full output goes to $outputFileName")

  writeOutputToFile()


  def printHelpAndExit(): Unit = {
    println(
      """Usage:
        |sbt "plainscala/run --help"
        |shows this message
        |
        |sbt "plainscala/run input.csv"
        |provide input file name, default is "input.csv"
        |
        |sbt "plainscala/run input.csv output"
        |provide both input and output file names, defaults are "input.csv", "output"
      """.stripMargin)
    System.exit(0)
  }

  def getTokens(lines: Iterator[String]): Iterator[String] = {
    lines flatMap Tokenizer.tokenize()
  }

  def getCounts(tokens: Iterator[String]): Map[String, Long] = {
    tokens.foldLeft(Map.empty[String, Long]) {
      (count, word) => count + (word -> (count.getOrElse(word, 0L) + 1))
    }
  }

  def printOutputSample(): Unit = {
    counts.take(10).foreach {
      case (token, count) => println(s"$token$OUTPUT_SEPARATOR$count")
    }
    println("...")
  }

  def writeOutputToFile(): Unit = {
    val writer = new PrintWriter(new File(outputFileName))
    counts.foreach {
      case (token, count) =>
        writer.print(token)
        writer.print(OUTPUT_SEPARATOR)
        writer.println(count)
    }
    writer.close()
  }
}
