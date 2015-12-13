package com.sageaxcess.sampletask.akka

import java.io.File

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import com.sageaxcess.sampletask.akka.Actors.{EOF, TokensCounter}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}

/**
  * Created by etsvigun on 12/13/15.
  */
class TokensCounterSuite(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with FunSuiteLike
  with BeforeAndAfter
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("tokenscount-test"))

  override def afterAll(): Unit = {
    system.terminate()
  }

  test("fails on invalid outut file names") {
    val nonExistingPath = s"/tmp/${java.util.UUID.randomUUID}/${java.util.UUID.randomUUID}"
    val parent = system.actorOf(Props(new Actor {
      val child = context.actorOf(Props(new TokensCounter(nonExistingPath)))

      def receive = {
        case x if sender == child => testActor forward x
        case x => child forward x
      }
    }))

    parent ! EOF
    expectMsgClass(classOf[Status.Failure])
  }

  test("counts tokens and outputs to file") {
    val f = File.createTempFile("sageaxcessakka", "test")
    val testedTokensCounter = system.actorOf(Props(new TokensCounter(f.getAbsolutePath, ":")))
    testedTokensCounter ! "aaa"
    testedTokensCounter ! "bbb"
    testedTokensCounter ! "aaa"
    testedTokensCounter ! EOF
    assert(io.Source.fromFile(f.getAbsolutePath).getLines().toSet == Set("aaa:2", "bbb:1"))
  }
}
