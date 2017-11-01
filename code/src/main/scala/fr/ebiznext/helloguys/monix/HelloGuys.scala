package fr.ebiznext.helloguys.monix

import monix.reactive.{Consumer, Observable}

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object HelloGuys extends App {
  def helloGuys(names: String*) = {
    Observable(names: _*).consumeWith(Consumer.foreach(n =>
      println(s"Hello $n !")))
  }
  import monix.execution.Scheduler.Implicits.global
  Await.result(helloGuys("Scala.IO", "guys").runAsync, Duration.Inf)
}
