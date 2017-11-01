package fr.ebiznext.cache.monix

import fr.ebiznext.cache.common.{BucketHandler, HttpStatusVal, URLVal}
import fr.ebiznext.cache.{CachedUrl, InvalidUrl, NotCachedUrl, URLBuckets}
import monix.reactive.Consumer

import scala.collection.immutable.Seq

trait Reporter[T] {
  def urlBucketsHandler: URLBuckets => T

  def report: Consumer[(URLVal, Either[Throwable, HttpStatusVal]), T] =
    Consumer
      .foldLeft[URLBuckets, (URLVal, Either[Throwable, HttpStatusVal])](
        (Seq[CachedUrl](), Seq[NotCachedUrl](), Seq[InvalidUrl]())) {
        case ((cachedUrl, notCachedUrl, invalidUrl), (url, eitherResponse)) =>
          eitherResponse.fold(
            _ => (cachedUrl, notCachedUrl, url.invalid +: invalidUrl),
            r =>
              if (r.isSuccess)
                (url.cached +: cachedUrl, notCachedUrl, invalidUrl)
              else
                (cachedUrl, url.notCached +: notCachedUrl, invalidUrl)
          )
      }
      .map(urlBucketsHandler)
}

class MessageReporter extends Reporter[String] {
  override def urlBucketsHandler: URLBuckets => String =
    BucketHandler.messageBuilderBucketHandler
}
