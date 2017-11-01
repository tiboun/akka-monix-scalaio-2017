package fr.ebiznext

import java.net.URL

import io.github.andrebeat.pool.Pool
import org.openqa.selenium.By
import org.openqa.selenium.remote.{DesiredCapabilities, RemoteWebDriver}

import scala.util.Try

object Crawler extends App {

  private val phantomJSPool = Pool(
    10,
    () => driver,
    dispose = (driver: RemoteWebDriver) => driver.close())

  private def driver: RemoteWebDriver = {
    val capabilities = new DesiredCapabilities()
    capabilities.setBrowserName("phantomjs")
    capabilities.setJavascriptEnabled(true)
    new RemoteWebDriver(new URL("http://localhost:9002"), capabilities)
  }

  private def crawl(urls: Set[String]): (Set[String], Set[String]) =
    crawl(urls, urls, Set())

  @scala.annotation.tailrec
  private def crawl(urls: Set[String],
                    visitedUrls: Set[String],
                    unvisitedUrls: Set[String]): (Set[String], Set[String]) = {
    if (urls.isEmpty)
      (visitedUrls, unvisitedUrls)
    else {
      val wholeNewUrls: Set[Either[String, Set[String]]] = urls.par
        .map { url =>
          phantomJSPool.acquire() { driver =>
            Try {
              driver.get(url)
              import scala.collection.JavaConverters._
              val links = driver
                .findElements(By.tagName("a"))
                .asScala
                .flatMap(l => Option(l.getAttribute("href")))
                .toSet
              val newUrls = (for {
                link <- links.toList
                url <- Try(new URL(link)).toOption
                if url.getHost == "localhost" && !visitedUrls(link)
              } yield {
                link
              }).toSet
              newUrls
            }.toEither.left.map(_ => url)
          }
        }
        .toList
        .toSet
      val (newInvalidUrls: Set[String], newValidUrls: Set[String]) =
        wholeNewUrls.foldLeft((Set[String](), Set[String]())) {
          case ((invalidUrls, validUrls), either) =>
            if (either.isLeft)
              (invalidUrls + either.left.get, validUrls)
            else
              (invalidUrls, validUrls ++ either.right.get)
        }
      crawl(newValidUrls,
            visitedUrls ++ newValidUrls,
            unvisitedUrls ++ newInvalidUrls)
    }
  }

  val start = System.currentTimeMillis()
  val (visitedUrls, unvisitedUrls) = crawl(Set("http://localhost:9001/"))
  println("crawling took : " + (System.currentTimeMillis() - start))
  println("unvisited urls")
  unvisitedUrls.foreach(println)
  println("visited urls")
  visitedUrls.foreach(println)
  phantomJSPool.close()
}
