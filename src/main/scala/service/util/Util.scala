package com.ubs.tri
package service.util
import java.nio.file.{Files, Path, Paths}
import scala.jdk.CollectionConverters.IteratorHasAsScala
import scala.util.{Failure, Success, Try}

object Util {
  def getFilePathsFromFolder(directoryPath: String): List[Path] = {
    Try {
      Files
        .newDirectoryStream(Paths.get(directoryPath))
        .iterator()
        .asScala
        .toList
    } match {
      case Success(files) => files
      case Failure(exception) => {
        println(s"directoryPath is not valid ${exception.getMessage}")
        List.empty[Path]
      }
    }
  }
}
