package fr.ebiznext.cache.common

case class HttpStatusVal(code: Int) {
  val isSuccess: Boolean = code >= 200 && code < 300
}
