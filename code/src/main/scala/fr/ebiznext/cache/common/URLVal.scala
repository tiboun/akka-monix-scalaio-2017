package fr.ebiznext.cache.common

sealed trait URLStatus
sealed trait Cached extends URLStatus
sealed trait NotCached extends URLStatus
sealed trait Invalid extends URLStatus

case class URLVal(value: String) {
  def invalid = new URLVal(value) with Invalid
  def cached = new URLVal(value) with Cached
  def notCached = new URLVal(value) with NotCached
}
