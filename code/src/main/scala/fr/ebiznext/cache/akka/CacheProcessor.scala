package fr.ebiznext.cache.akka

import akka.stream.Materializer
import scala.concurrent.{ExecutionContext, Future}

class CacheProcessor[T](urlFeeder: URLFeeder,
                        httpClient: HttpClient,
                        reporter: Reporter[T])(
    implicit materializer: Materializer) {
  def cache: Future[T] =
    urlFeeder.url.via(httpClient.httpGet).runWith(reporter.report)
}
