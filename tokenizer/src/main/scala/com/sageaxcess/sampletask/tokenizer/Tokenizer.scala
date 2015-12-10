package com.sageaxcess.sampletask.tokenizer

/**
  * Tokens processing
  */
object Tokenizer {
  val DEFAULT_SEPARATOR = ','

  /**
    * Splits lines into tokens using the separator character provided
    * @param row line to split, normally a line from a CSV file
    * @param separator separator used to split liens
    * @return tokens acquired
    */
  def tokenize(separator: Char = DEFAULT_SEPARATOR)(row: String): Iterable[String] = {
    row.split(separator)
  }
}
