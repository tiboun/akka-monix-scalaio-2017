package fr.ebiznext.cache.akka

import java.nio.file.NoSuchFileException
import akka.stream.scaladsl.Sink
import fr.ebiznext.cache.UTestExt
import fr.ebiznext.cache.common.FileUrlFeederConfig
import utest._

object URLFeederTests extends TestSuite with AkkaModule with UTestExt {
  override def tests = Tests {
    import system.dispatcher
    "FileURLFeeder should" - {
      "read 3 URLs" - {
        val linksPath = getClass.getClassLoader.getResource("links.txt").getPath
        val fileUrlFeeder =
          new FileURLFeeder(FileUrlFeederConfig(linksPath, "\n"))
        fileUrlFeeder.url.runWith(Sink.seq) map { urls =>
          assert(urls.size == 3)
        }
      }
      "fail" - {
        "when path is invalid" - {
          recoverToSucceededIf[NoSuchFileException] {
            val fileUrlFeeder =
              new FileURLFeeder(FileUrlFeederConfig("/invalidpathfile", "\n"))

            fileUrlFeeder.url.runWith(Sink.seq)
          }
        }
      }
    }
  }
}