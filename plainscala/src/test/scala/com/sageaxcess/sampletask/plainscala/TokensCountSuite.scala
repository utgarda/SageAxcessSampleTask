package com.sageaxcess.sampletask.plainscala
import org.scalatest.FunSuite

/**
  * Basic lines and tokens processing tests,
  * I/O is not tested
  */
class TokensCountSuite extends FunSuite {

  test("lines processing is ok with empty input") {
    assert(TokensCount.getTokens(Iterator.empty).toList == List.empty[String])
  }

  test("tokens processing is ok with empty input") {
    assert(TokensCount.getCounts(Iterator.empty) == Map.empty[String, Long])
  }

  val lines = List(
    "aaa,bbb,ccc",
    "aaa,bbb,line with spaces")
  val tokens = List("aaa", "bbb", "ccc", "aaa", "bbb", "line with spaces")
  val counts = Map[String, Long]("aaa" -> 2, "bbb" -> 2, "ccc" -> 1, "line with spaces" -> 1)

  test("splits lines to tokens") {
    assert(TokensCount.getTokens(lines.toIterator).toList == tokens)
  }

  test("counts tokens") {
    assert(TokensCount.getCounts(tokens.toIterator) == counts)
  }
}
