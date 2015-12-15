package com.sageaxcess.sampletask.spark

import org.apache.spark.{SparkConf, SparkContext}
import org.scalatest.{BeforeAndAfter, FunSuite, GivenWhenThen}

/**
  * Testing RDD transformations for TokensCount
  */
class ToknesCountTransformationsSuite
  extends FunSuite
  with BeforeAndAfter
  with GivenWhenThen {

  private val AppName = "stream-aggregation-test"
  var sc: SparkContext = _

  before {
    System.clearProperty("spark.driver.port")
    System.clearProperty("spark.hostPort")
    val conf = new SparkConf()
      .setMaster("local")
      .setAppName(AppName)

    sc = new SparkContext(conf)
  }

  after {
    sc.stop()
    System.clearProperty("spark.driver.port")
    System.clearProperty("spark.hostPort")
  }

  test("tokenizes lines") {
    val lines = sc.parallelize(List("aaa,bbb,ccc", "aaa,bbb,ddd"))
    val tokens = TokensCountTransformations.tokenize(lines)
    assert(tokens.collect() sameElements Array("aaa", "bbb", "ccc", "aaa", "bbb", "ddd"))
  }

  test("counts tokens") {
    val tokens = sc.parallelize(List("aaa", "bbb", "ccc", "aaa", "bbb", "ddd"))
    val counts = TokensCountTransformations.count(tokens)
    assert(counts.collectAsMap() == Map("aaa" -> 2L, "bbb" -> 2L, "ccc" -> 1L, "ddd" -> 1L))
  }

}
