package com.sageaxcess.sampletask.spark

import com.sageaxcess.sampletask.tokenizer.Tokenizer
import org.apache.spark.rdd.RDD

/**
  * RDD transformations for TokensCount
  */
object TokensCountTransformations {

  def tokenize(lines: RDD[String]): RDD[String] ={
    lines flatMap Tokenizer.tokenize()
  }

  def count(tokens: RDD[String]): RDD[(String, Long)] = {
    tokens.map(x => (x, 1L)).reduceByKey(_ + _)
  }
}
