package com.sageaxcess.sampletask.tokenizer

import org.scalatest.FunSuite

/**
  * Lines processing test
  */
class TokenizerSuite extends FunSuite {
  test("is ok with empty lines") {
    assert(Tokenizer.tokenize()("").toList == List(""))
  }

  test("splits lines into tokens") {
    assert(Tokenizer.tokenize()("a,b,c").toList == List("a", "b", "c"))
  }

  test("accepts custom separators") {
    assert(Tokenizer.tokenize('|')("a|b|c").toList == List("a", "b", "c"))
  }
}
