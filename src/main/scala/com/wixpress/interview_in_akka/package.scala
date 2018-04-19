package com.wixpress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

import scala.concurrent.Await
import scala.concurrent.duration.Duration

package object interview_in_akka {

  implicit val system: ActorSystem = ActorSystem("hello")
  sys.addShutdownHook(Await.result(system.terminate(), Duration.Inf))

  implicit val mat: ActorMaterializer = ActorMaterializer()

}
