package fr.ebiznext.cache.common

sealed trait UrlFeederConfig
case class FileUrlFeederConfig(fileLocation: String, lineDelimiter: String)
    extends UrlFeederConfig
