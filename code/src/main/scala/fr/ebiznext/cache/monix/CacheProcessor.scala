package fr.ebiznext.cache.monix

import monix.execution.{CancelableFuture, Scheduler}

class CacheProcessor[T](urlFeeder: URLFeeder,
                        httpClient: HttpClient,
                        reporter: Reporter[T])(implicit scheduler: Scheduler) {
  def cache: CancelableFuture[T] =
    urlFeeder.url
      .transform(httpClient.httpGet)
      .consumeWith(reporter.report)
      .runAsync
}
