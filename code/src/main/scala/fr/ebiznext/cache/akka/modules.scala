package fr.ebiznext.cache.akka

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Source
import akka.util.ByteString
import com.softwaremill.macwire.wire
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.akkahttp.AkkaHttpBackend
import com.typesafe.config.ConfigFactory
import configs.Configs
import fr.ebiznext.cache.{CachedUrl, InvalidUrl, NotCachedUrl, URLBuckets}
import fr.ebiznext.cache.common.{
  BucketHandler,
  FileUrlFeederConfig,
  HttpClientConfig
}

import scala.collection.immutable
import scala.concurrent.Future

trait AkkaModule {
  implicit val system: ActorSystem = AkkaModule.actorSystem
  implicit val materializer: ActorMaterializer = AkkaModule.actorMaterializer
}

trait HttpModule extends AkkaModule {
  val httpClientConfig: HttpClientConfig =
    Configs[HttpClientConfig]
      .get(ConfigFactory.load(), "cache.http-client")
      .value
  implicit val httpBackend: SttpBackend[Future, Source[ByteString, Any]] =
    AkkaHttpBackend.usingActorSystem(system)
  val httpClient: SttpHttpClient = wire[SttpHttpClient]
}

trait AppModule extends HttpModule {
  import system.dispatcher
  val fileUrlFeederConfig: FileUrlFeederConfig =
    Configs[FileUrlFeederConfig]
      .get(ConfigFactory.load(), "cache.file-url-feeder")
      .value
  val urlFeeder: FileURLFeeder = wire[FileURLFeeder]
  val reporter: MessageReporter = wire[MessageReporter]
  val cacheProcessor: CacheProcessor[String] =
    wire[CacheProcessor[String]]
}

object AkkaModule {
  implicit val actorSystem: ActorSystem = ActorSystem()
  implicit val actorMaterializer: ActorMaterializer = ActorMaterializer()
}
