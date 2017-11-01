package fr.ebiznext.cache.monix

import java.nio.ByteBuffer

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.softwaremill.macwire.wire
import com.softwaremill.sttp.SttpBackend
import com.softwaremill.sttp.asynchttpclient.monix.AsyncHttpClientMonixBackend
import com.typesafe.config.ConfigFactory
import configs.Configs
import fr.ebiznext.cache.URLBuckets
import fr.ebiznext.cache.common.{
  BucketHandler,
  FileUrlFeederConfig,
  HttpClientConfig,
  URLVal
}
import monix.eval.Task
import monix.reactive.Observable

import scala.collection.immutable

trait HttpModule {
  val httpClientConfig: HttpClientConfig =
    Configs[HttpClientConfig]
      .get(ConfigFactory.load(), "cache.http-client")
      .value
  implicit val httpBackend: SttpBackend[Task, Observable[ByteBuffer]] =
    AsyncHttpClientMonixBackend()
  val httpClient: SttpHttpClient = wire[SttpHttpClient]
}

trait AppModule extends HttpModule {
  import monix.execution.Scheduler.Implicits.global
  val fileUrlFeederConfig: FileUrlFeederConfig =
    Configs[FileUrlFeederConfig]
      .get(ConfigFactory.load(), "cache.file-url-feeder")
      .value
  val urlFeeder: FileURLFeeder = wire[FileURLFeeder]
  val bucketHandler: URLBuckets => String =
    BucketHandler.messageBuilderBucketHandler
  val reporter: Reporter[String] = wire[MessageReporter]
  val cacheProcessor: CacheProcessor[String] =
    wire[CacheProcessor[String]]
}
