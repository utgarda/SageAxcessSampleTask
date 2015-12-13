package com.sageaxcess.sampletask.akka

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.testkit.{ImplicitSender, TestKit}
import com.sageaxces.sampletask.akka.Actors.{ChangeSeparator, LinesTokenizer}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}

/**
  * Testing lines tokenizer - splitting lines with default and custom separators
  */
class LinesTokenizerSuite(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with FunSuiteLike
  with BeforeAndAfter
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("tokenscount-test"))

  val testActorPath = testActor.path.toString

  var testedLinesTokenizer: ActorRef = null

  before {
    testedLinesTokenizer = system.actorOf(Props(new LinesTokenizer(testActorPath)))
  }

  override def afterAll(): Unit = {
    system.terminate()
  }

  test("splits lines with default separator") {
    testedLinesTokenizer ! "a,b,c"
    expectMsg[String]("a")
    expectMsg[String]("b")
    expectMsg[String]("c")
  }

  test("changes separaor on request") {
    testedLinesTokenizer ! ChangeSeparator('|')
    testedLinesTokenizer ! "a,b,c|d"
    expectMsg[String]("a,b,c")
    expectMsg[String]("d")
  }
}
