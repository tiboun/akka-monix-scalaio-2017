package fr.ebiznext.cache.monix

import java.nio.ByteBuffer

import com.softwaremill.sttp.testing.SttpBackendStub
import fr.ebiznext.cache.UTestExt
import fr.ebiznext.cache.common.URLVal
import monix.eval.Task
import monix.reactive.{Consumer, Observable}
import utest._

object HttpClientTests extends TestSuite with UTestExt with HttpModule {

  import monix.execution.Scheduler.Implicits.global

  override def tests = Tests {
    "HttpClient should" - {
      val sttpBackendStub: SttpBackendStub[Task, Observable[ByteBuffer]] =
        SttpBackendStub(httpBackend)
          .whenRequestMatches(_.uri.path.startsWith(List("goodurl")))
          .thenRespondOk()
          .whenRequestMatches(_.uri.path.startsWith(List("badurl")))
          .thenRespondServerError()

      val sttpClient = new SttpHttpClient(httpClientConfig)(sttpBackendStub)
      "be a successful http code" - {
        (Observable
          .pure(URLVal("http://gooddomain.com/goodurl"))
          .transform(sttpClient.httpGet)
          .consumeWith(Consumer.head)
          .runAsync)
          .map {
            case (_, either) =>
              assert(either.isRight)
              assert(either.right.get.isSuccess)
          }
      }
      "be an unsuccessful http code" - {
        (Observable
          .pure(URLVal("http://gooddomain.com/badurl"))
          .transform(sttpClient.httpGet)
          .consumeWith(Consumer.head)
          .runAsync)
          .map {
            case (_, either) =>
              assert(either.isRight)
              assert(!either.right.get.isSuccess)
          }
      }
      "fail" - {
        val sttpClient = new SttpHttpClient(httpClientConfig)
        "when domain is unknown" - {
          (Observable
            .pure(URLVal("http://unknownhost.com/"))
            .transform(sttpClient.httpGet)
            .consumeWith(Consumer.head)
            .runAsync)
            .map {
              case (_, either) => assert(either.isLeft)
            }
        }
      }
    }
  }
}
