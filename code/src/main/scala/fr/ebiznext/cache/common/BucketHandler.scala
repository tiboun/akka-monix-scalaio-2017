package fr.ebiznext.cache.common

import fr.ebiznext.cache.URLBuckets

import scala.collection.immutable.Seq

object BucketHandler {
  def listUrls(urls: Seq[URLVal]): String = urls.mkString("  - ", "\n  - ", "\n")
  val messageBuilderBucketHandler: URLBuckets => String =
    {
      case (cachedUrl, notCachedUrl, invalidUrl) =>
        val sb = new StringBuilder()
        sb.append(s"${cachedUrl.size} urls cached\n")
        if (notCachedUrl.nonEmpty) {
          sb.append(s"${notCachedUrl.size} urls haven't been cached :\n")
          sb.append(listUrls(notCachedUrl))
        }
        if (invalidUrl.nonEmpty) {
          sb.append(s"${invalidUrl.size} invalid urls:\n")
          sb.append(listUrls(invalidUrl))
        }
        sb.toString()
    }
}
