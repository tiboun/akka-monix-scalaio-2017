package fr.ebiznext.cache.monix

import java.nio.ByteBuffer

import com.softwaremill.sttp._
import fr.ebiznext.cache.common.{HttpClientConfig, HttpStatusVal, URLVal}
import monix.eval.Task
import monix.reactive.Observable
import monix.reactive.observables.ObservableLike.Transformer

trait HttpClient {
  def httpGet
    : Transformer[URLVal, (URLVal, Either[Throwable, HttpStatusVal])]
}

class SttpHttpClient(httpClientConfig: HttpClientConfig)(
    implicit monixHttpBackend: SttpBackend[Task, Observable[ByteBuffer]])
    extends HttpClient {

  override val httpGet: Transformer[URLVal, (URLVal, Either[Throwable, HttpStatusVal])] =
    _.mapAsync(httpClientConfig.maxClient)(url =>
      get(url).map(url -> _))

  private def get(url: URLVal): Task[Either[Throwable, HttpStatusVal]] = {
    sttp
      .get(StringContext(url.value).uri())
      .send()
      .map(r => Right(HttpStatusVal(r.code)))
      .onErrorRecover {
        case t: Throwable => Left(t)
      }
  }

}
