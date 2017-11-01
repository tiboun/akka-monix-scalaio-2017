package fr.ebiznext.cache.monix

import com.typesafe.config.{Config, ConfigFactory}

import scala.util.{Failure, Success}

object Main extends App with AppModule {
  import monix.execution.Scheduler.Implicits.global
  val config: Config = ConfigFactory.load()
  cacheProcessor.cache onComplete {
    case Failure(e) =>
      e.printStackTrace()
      httpBackend.close()
    case Success(message) =>
      println(message)
      httpBackend.close()
  }
}
