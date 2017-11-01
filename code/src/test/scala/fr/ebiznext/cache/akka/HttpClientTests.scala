package fr.ebiznext.cache.akka

import akka.stream.scaladsl.{Sink, Source}
import akka.util.ByteString
import com.softwaremill.sttp.testing.SttpBackendStub
import com.softwaremill.sttp.{HttpURLConnectionBackend, Id, MonadError, Request, Response, SttpBackend}
import fr.ebiznext.cache.UTestExt
import fr.ebiznext.cache.common.URLVal
import utest._

import scala.concurrent.Future

object HttpClientTests extends TestSuite with AkkaModule with UTestExt with HttpModule {

  import system.dispatcher

  override def tests = Tests {
    "HttpClient should" - {
      val sttpBackendStub: SttpBackendStub[Future, Source[ByteString, Any]] = SttpBackendStub(httpBackend)
        .whenRequestMatches(_.uri.path.startsWith(List("goodurl")))
        .thenRespondOk()
        .whenRequestMatches(_.uri.path.startsWith(List("badurl")))
        .thenRespondServerError()

      val sttpClient = new SttpHttpClient(httpClientConfig)(sttpBackendStub)
      "be a successful http code" - {
        Source.single(URLVal("http://gooddomain.com/goodurl"))
          .via(sttpClient.httpGet)
          .runWith(Sink.head).map {
          case (_, either) =>
            assert(either.isRight)
            assert(either.right.get.isSuccess)
        }
      }
      "be an unsuccessful http code" - {
        Source.single(URLVal("http://gooddomain.com/badurl"))
          .via(sttpClient.httpGet)
          .runWith(Sink.head).map {
          case (_, either) =>
            assert(either.isRight)
            assert(!either.right.get.isSuccess)
        }
      }
      "fail" - {
        val sttpClient = new SttpHttpClient(httpClientConfig)
        "when domain is unknown" - {
          Source.single(URLVal("http://unknownhost.com/"))
            .via(sttpClient.httpGet)
            .runWith(Sink.head).map {
            case (_, either) => assert(either.isLeft)
          }
        }
      }
    }
  }
}