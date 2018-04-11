package com.wixpress.interview_in_akka

import akka.stream.scaladsl.Source

object FibonacciApp extends App {

  def fib: Source[Int, _] = Source.unfold(0 → 1) {
    case (prev, cur) ⇒
      val state = cur → (prev + cur)
      val out = prev

      Some(state → out)
  }

  fib.zipWithIndex
    .map(_.swap)
    .take(10)
    .runForeach(println)

}

// - how to run a stream
// - show that stream runs in different process