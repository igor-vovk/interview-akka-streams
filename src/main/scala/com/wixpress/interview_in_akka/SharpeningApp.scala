package com.wixpress.interview_in_akka

import akka.stream.scaladsl.{Flow, Sink, Source}

import scala.collection.immutable.Seq
import scala.concurrent.{Future, blocking}

trait Processor {

  def load(filename: String): Array[Byte]

  def process(image: Array[Byte]): Array[Byte]

  def save(filename: String, image: Array[Byte]): Unit

}

object SharpeningApp {

  val files = Seq("1.jpg", "2.jpg", "3.jpg")
  val processor: Processor = ???

  // Basic example
  Source(files)
    .map(filename ⇒ filename → processor.load(filename))
    .map { case (filename, image) ⇒ filename → processor.process(image) }
    .runForeach((processor.save _).tupled)

  // Add parallelism
  Source(files)
    .map (filename ⇒ filename → processor.load(filename))
    .mapAsyncUnordered(4) { case (filename, image) ⇒
      Future(blocking {
        filename → processor.process(image)
      })
    }
    .runForeach((processor.save _).tupled)

  // Extract definitions
  def items[A](iterator: Iterator[A]) = Source.fromIterator(() ⇒ iterator)

  def load = Flow[String].map(processor.load)

  def process = Flow[Array[Byte]].map(processor.process)

  def save = Sink.foreach((processor.save _).tupled)

  items(files.iterator)
    .via(load)
    .via(process)
    .runWith(save)

}
