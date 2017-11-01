package fr.ebiznext.cache.akka

import akka.stream.scaladsl.Source
import com.softwaremill.sttp.Response
import fr.ebiznext.cache.common.{HttpStatusVal, URLVal}
import fr.ebiznext.cache.{CachedUrl, InvalidUrl, NotCachedUrl, URLBuckets}
import utest._

import scala.collection.immutable.Seq

object ReporterTests extends TestSuite with AkkaModule {
  override def tests = Tests {
    import system.dispatcher
    val reporter
      : Reporter[(Seq[CachedUrl], Seq[NotCachedUrl], Seq[InvalidUrl])] =
      new Reporter[URLBuckets] {
        override val urlBucketsHandler: URLBuckets => URLBuckets = identity
      }
    "Reporter should" - {
      "fill 'cached' bucket only" - {
        val links: Seq[(URLVal, Either[Throwable, HttpStatusVal])] =
          List(URLVal("http://dummy.com/link1") -> Right(HttpStatusVal(200)))
        Source(links).runWith(reporter.report) map {
          case (cachedUrl, notCachedUrl, invalidUrl) =>
            assert(cachedUrl.size == 1)
            assert(notCachedUrl.isEmpty)
            assert(invalidUrl.isEmpty)
        }
      }
      "fill 'not cached' bucket only" - {
        val links: Seq[(URLVal, Either[Throwable, HttpStatusVal])] =
          List(
            URLVal("http://dummy.com/link2") -> Right(HttpStatusVal(404))
          )
        Source(links).runWith(reporter.report) map {
          case (cachedUrl, notCachedUrl, invalidUrl) =>
            assert(cachedUrl.isEmpty)
            assert(notCachedUrl.size == 1)
            assert(invalidUrl.isEmpty)
        }
      }
      "fill 'invalid' bucket only" - {
        val links: Seq[(URLVal, Either[Throwable, HttpStatusVal])] =
          List(
            URLVal("http://dummy.com/link3") -> Left(
              new RuntimeException("Invalid link"))
          )
        Source(links).runWith(reporter.report) map {
          case (cachedUrl, notCachedUrl, invalidUrl) =>
            assert(cachedUrl.isEmpty)
            assert(notCachedUrl.isEmpty)
            assert(invalidUrl.size == 1)
        }
      }
      "not fill buckets" - {
        val links: Seq[(URLVal, Either[Throwable, HttpStatusVal])] =
          List()
        Source(links).runWith(reporter.report) map {
          case (cachedUrl, notCachedUrl, invalidUrl) =>
            assert(cachedUrl.isEmpty)
            assert(notCachedUrl.isEmpty)
            assert(invalidUrl.isEmpty)
        }
      }
      "fill all buckets" - {
        val links: Seq[(URLVal, Either[Throwable, HttpStatusVal])] =
          List(
            URLVal("http://dummy.com/link1") -> Right(HttpStatusVal(200)),
            URLVal("http://dummy.com/link2") -> Right(HttpStatusVal(404)),
            URLVal("http://dummy.com/link3") -> Left(
              new RuntimeException("Invalid link"))
          )
        Source(links).runWith(reporter.report) map {
          case (cachedUrl, notCachedUrl, invalidUrl) =>
            assert(cachedUrl.size == 1)
            assert(notCachedUrl.size == 1)
            assert(invalidUrl.size == 1)
        }
      }
    }
  }
}
