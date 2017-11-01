package fr.ebiznext.cache.akka
import scala.util.{Failure, Success}

object Main extends App with AppModule {
  import system.dispatcher
  cacheProcessor.cache onComplete {
    case Failure(e) =>
      e.printStackTrace()
      system.terminate()
    case Success(message) =>
      println(message)
      system.terminate()
  }
}
