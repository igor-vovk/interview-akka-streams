package com.wixpress

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

package object interview_in_akka {

  implicit val system: ActorSystem = ActorSystem("hello")

  implicit val mat: ActorMaterializer = ActorMaterializer()

}
