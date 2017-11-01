package fr.ebiznext.cache.monix

import java.io.FileNotFoundException

import fr.ebiznext.cache.UTestExt
import fr.ebiznext.cache.common.{FileUrlFeederConfig, URLVal}
import monix.reactive.Consumer
import utest._

import scala.util.Try

object URLFeederTests extends TestSuite with UTestExt {

  import monix.execution.Scheduler.Implicits.global

  override def tests = Tests {
    "FileURLFeeder should" - {
      "read 3 URLs" - {
        val linksPath = getClass.getClassLoader.getResource("links.txt").getPath
        val fileUrlFeeder =
          new FileURLFeeder(FileUrlFeederConfig(linksPath, "\n"))
        (fileUrlFeeder.url
          .consumeWith(
            Consumer.foldLeft(List[URLVal]())((acc, el) => el +: acc))
          .runAsync) map { urls =>
          assert(urls.size == 3)
        }
      }
      "fail" - {
        "when path is invalid" - {
          intercept[FileNotFoundException] {
            new FileURLFeeder(FileUrlFeederConfig("/invalidpathfile", "\n"))
          }
        }
      }
    }
  }
}
