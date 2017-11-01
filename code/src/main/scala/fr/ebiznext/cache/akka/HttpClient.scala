package fr.ebiznext.cache.akka

import akka.NotUsed
import akka.stream.scaladsl.{Flow, Source}
import akka.util.ByteString
import com.softwaremill.sttp._
import fr.ebiznext.cache.common.{HttpClientConfig, HttpStatusVal, URLVal}

import scala.concurrent.Future

trait HttpClient {
  def httpGet: Flow[URLVal, (URLVal, Either[Throwable, HttpStatusVal]), NotUsed]
}

class SttpHttpClient(httpClientConfig: HttpClientConfig)(
    implicit akkaHttpBackend: SttpBackend[Future, Source[ByteString, Any]])
    extends HttpClient {

  import scala.concurrent.ExecutionContext.Implicits.global

  private def get(url: URLVal): Future[Either[Throwable, HttpStatusVal]] = {
    sttp
      .get(StringContext(url.value).uri())
      .send()
      .map(r => Right(HttpStatusVal(r.code)))
      .recover { case t: Throwable => Left(t) }
  }

  override val httpGet: Flow[URLVal, (URLVal, Either[Throwable, HttpStatusVal]), NotUsed] =
    Flow[URLVal].mapAsyncUnordered(httpClientConfig.maxClient)(url =>
      get(url).map(url -> _))
}
