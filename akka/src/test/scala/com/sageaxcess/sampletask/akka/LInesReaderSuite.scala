package com.sageaxcess.sampletask.akka

import java.io.{File, PrintWriter}

import akka.actor._
import akka.testkit.{ImplicitSender, TestKit}
import com.sageaxces.sampletask.akka.Actors.{EOF, InvalidFileException, LinesReader, ReadFile}
import org.scalatest.{BeforeAndAfter, BeforeAndAfterAll, FunSuiteLike}

/**
  * Testing lines reading
  */
class LinesReaderSuite(_system: ActorSystem)
  extends TestKit(_system)
  with ImplicitSender
  with FunSuiteLike
  with BeforeAndAfter
  with BeforeAndAfterAll {

  def this() = this(ActorSystem("tokenscount-test"))

  val testActorPath = testActor.path.toString

  var f: File = null

  var testedLinesReader: ActorRef = null

  before {
    f = File.createTempFile("sageaxcessakka", "test")
    testedLinesReader = system.actorOf(Props(new LinesReader(testActorPath)))
  }

  after {
    f.delete()
    system.stop(testedLinesReader)
  }

  override def afterAll(): Unit = {
    system.terminate()
  }


  test("fails on empty files") {
    testedLinesReader ! ReadFile(f.getAbsolutePath)
    expectMsg[Status.Failure](Status.Failure(InvalidFileException(f.getAbsolutePath)))
  }

  test("tokens processing is ok with empty input") {
    val writer = new PrintWriter(f)
    writer.println("HeaderA,HeaderB,HeaderC")
    writer.println("a,b,c")
    writer.println("a,b,d")
    writer.close()

    testedLinesReader ! ReadFile(f.getAbsolutePath)
    expectMsg[String]("a,b,c")
    expectMsg[String]("a,b,d")
    expectMsg[EOF.type](EOF)
  }
}
