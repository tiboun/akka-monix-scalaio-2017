package fr.ebiznext.helloguys.akka

import akka.Done
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Keep, RunnableGraph, Sink, Source}

import scala.concurrent.Future

object HelloGuys extends App {
  def helloGuys(names: String*): RunnableGraph[Future[Done]] = {
    Source(names.toList)
      .toMat(Sink.foreach(n => println(s"Hello $n !")))(Keep.right)
  }
  implicit val actorSystem: ActorSystem = ActorSystem("hello-guys-app")
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
  import actorSystem.dispatcher
  helloGuys("Scala.IO", "guys").run().onComplete(_ => actorSystem.terminate())
}
