package fr.ebiznext.cache.monix

import java.io._
import java.nio.charset.StandardCharsets

import fr.ebiznext.cache.common.{FileUrlFeederConfig, URLVal}
import monix.reactive.Observable

trait URLFeeder {
  def url: Observable[URLVal]
}

class FileURLFeeder(fileUrlFeederConfig: FileUrlFeederConfig)
    extends URLFeeder {
  override val url: Observable[URLVal] = Observable
    .fromLinesReader(
      new BufferedReader(
        new InputStreamReader(
          new FileInputStream(fileUrlFeederConfig.fileLocation),
          StandardCharsets.UTF_8)))
    .map(URLVal)
}
