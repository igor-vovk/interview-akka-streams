package com.wixpress.interview_in_akka

import akka.stream.scaladsl._

import scala.concurrent.{Future, blocking}

trait Processor {

  def load(filename: String): Array[Byte]

  def process(image: Array[Byte]): Array[Byte]

  def save(filename: String, image: Array[Byte]): Unit

}

object SharpeningApp {

  val processor: Processor = ???

  def loadIndex(): Iterator[String] = scala.io.Source.fromFile("a.csv").getLines

  // Basic example
  Source.fromIterator(loadIndex)
    .map {
      filename => filename → processor.load(filename)
    }
    .map { case (filename, image) ⇒
      filename → processor.process(image)
    }
    .runForeach((processor.save _).tupled)


  // Add parallelism
  Source.fromIterator(loadIndex)
    .map { filename =>
      filename → processor.load(filename)
    }
    .mapAsyncUnordered(4) { case (filename, image) ⇒
      Future(blocking {
        filename → processor.process(image)
      })
    }
    .runForeach((processor.save _).tupled)


  // Extract definitions
  def items[A](iterator: Iterator[A]) = Source.fromIterator(() ⇒ iterator)

  def loadItem = Flow[String].map(processor.load)

  def process = Flow[Array[Byte]].map(processor.process)

  def saveItem = Sink.foreach((processor.save _).tupled)

  items(loadIndex())
    .via(loadItem)
    .via(process)
    .runWith(saveItem)

  // Show differences in composition between function calls, monads and Streams




  // Use GraphBuilder API




}
