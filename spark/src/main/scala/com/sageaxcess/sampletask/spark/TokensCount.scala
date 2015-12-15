package com.sageaxcess.sampletask.spark

import java.io.{File, PrintWriter}

import org.apache.spark.{SparkConf, SparkContext}

/**
  * Spark-based tokens count for SageAxcess test task
  * not exactly a prod setup, input and output files read/written to local filesystems to keep it simple.
  */
object TokensCount {
  private val AppName = "TokensCount"
  private val OUTPUT_SEPARATOR = ":"
  private val DEFAULT_INPUT = "file:///tmp/input.csv"
  private val DEFAULT_OUTPUT = "/tmp/output"

  def main(args: Array[String]) {
    val inputFileName = if (args.nonEmpty) {
      s"file://${args(0)}"
    } else DEFAULT_INPUT
    val outputFileName = if (args.length > 1) {
      args(1)
    } else DEFAULT_OUTPUT

    val conf = new SparkConf().setAppName(AppName)
    val sc = new SparkContext(conf)


    val file = sc.textFile(inputFileName)
    //dropping header
    val csvLines = file.mapPartitionsWithIndex { (idx, iter) =>
      if (idx == 0) iter.drop(1) else iter
    }

    val tokens = TokensCountTransformations.tokenize(csvLines)

    val tokenCounts = TokensCountTransformations.count(tokens)

    val counts = tokenCounts.collect()

    sc.stop()


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
