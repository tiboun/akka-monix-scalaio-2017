package fr.ebiznext.cache.akka

import akka.stream.scaladsl.{Flow, Keep, Sink}
import fr.ebiznext.cache.common.{BucketHandler, HttpStatusVal, URLVal}
import fr.ebiznext.cache.{CachedUrl, InvalidUrl, NotCachedUrl, URLBuckets}

import scala.collection.immutable.Seq
import scala.concurrent.Future

trait Reporter[T] {
  def urlBucketsHandler: URLBuckets => T
  lazy val report: Sink[(URLVal, Either[Throwable, HttpStatusVal]), Future[T]] =
    Flow[(URLVal, Either[Throwable, HttpStatusVal])]
      .fold((Seq[CachedUrl](), Seq[NotCachedUrl](), Seq[InvalidUrl]())) {
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
      .toMat(Sink.head)(Keep.right)
}

class MessageReporter extends Reporter[String] {
  override val urlBucketsHandler: URLBuckets => String = BucketHandler.messageBuilderBucketHandler
}
