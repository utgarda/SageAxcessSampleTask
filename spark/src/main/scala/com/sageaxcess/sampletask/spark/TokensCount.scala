package com.sageaxcess.sampletask.spark

import java.io.{File, PrintWriter}

import com.sageaxcess.sampletask.tokenizer.Tokenizer
import org.apache.spark.{SparkConf, SparkContext}

/**
  * Created by etsvigun on 12/14/15.
  */
object TokensCount {
  private val AppName = "TokensCount"
  private val OUTPUT_SEPARATOR = ":"
  private val DEFAULT_INPUT = "file:///tmp/input.csv"
  private val DEFAULT_OUTPUT = "/tmp/output"

  def main(args: Array[String]) {
    val conf = new SparkConf().setAppName(AppName)
    val sc = new SparkContext(conf)


    val file = sc.textFile(DEFAULT_INPUT)
    //dropping header
    val csvLines = file.mapPartitionsWithIndex { (idx, iter) =>
      if (idx == 0) iter.drop(1) else iter
    }
    val words = csvLines flatMap Tokenizer.tokenize()

    val wordCounts = words.map(x => (x, 1)).reduceByKey(_ + _)

    val counts = wordCounts.collect()

    sc.stop()


    val writer = new PrintWriter(new File(DEFAULT_OUTPUT))
    counts.foreach {
      case (token, count) =>
        writer.print(token)
        writer.print(OUTPUT_SEPARATOR)
        writer.println(count)
    }
    writer.close()

  }
}