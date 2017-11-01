package fr.ebiznext

import fr.ebiznext.cache.common._

import scala.collection.immutable.Seq

package object cache {
  type InvalidUrl = URLVal with Invalid
  type CachedUrl = URLVal with Cached
  type NotCachedUrl = URLVal with NotCached
  type URLBuckets = (Seq[CachedUrl], Seq[NotCachedUrl], Seq[InvalidUrl])
}
