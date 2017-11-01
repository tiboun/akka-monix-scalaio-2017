package fr.ebiznext.cache.akka

import java.io.File

import akka.NotUsed
import akka.stream.scaladsl.{FileIO, Framing, Source}
import akka.util.ByteString
import fr.ebiznext.cache.common.{FileUrlFeederConfig, URLVal}

trait URLFeeder {
  def url: Source[URLVal, NotUsed]
}

class FileURLFeeder(fileUrlFeederConfig: FileUrlFeederConfig)
    extends URLFeeder {
  override val url: Source[URLVal, NotUsed] = FileIO
    .fromPath(new File(fileUrlFeederConfig.fileLocation).toPath)
    .via(
      Framing.delimiter(ByteString("\n"),
                        maximumFrameLength = 256,
                        allowTruncation = true))
    .map(bs => URLVal(bs.utf8String))
    .mapMaterializedValue(_ => NotUsed)
}
