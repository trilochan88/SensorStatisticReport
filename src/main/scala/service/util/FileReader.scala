package com.ubs.tri
package service.util

import model.{ProcessedFileData, SensorData}

import cats.effect.IO

import java.nio.file.{Files, Path}
import scala.collection.mutable.ListBuffer
import scala.io.Source

trait FileReader {
  def readFileData(): IO[ProcessedFileData]
}

object FileReader {
  def apply(filePaths: List[Path]): Option[FileReader] = {
    val csvFiles = filePaths.filter(f => isCsvFile(f))
    if (csvFiles.nonEmpty) {
      Some(new CSVFileReader(csvFiles))
    } else {
      None
    }
  }
  def isCsvFile(file: Path): Boolean = {
    Files.probeContentType(file) match {
      case null => false
      case mime => mime == "text/csv"
    }
  }
}

class CSVFileReader(csvFilePaths: List[Path]) extends FileReader {
  override def readFileData(): IO[ProcessedFileData] = {
    val sensorDataList = new ListBuffer[SensorData]
    var count = 0
    csvFilePaths.foreach { file =>
      count += 1
      val source = Source.fromFile(file.toString)
      val lines = source.getLines().drop(1)
      for (line <- lines) {
        val Array(sensor_id, humidity) = line.split(",").map(_.trim)
        sensorDataList += SensorData(sensor_id, parseInt(humidity))
      }
      source.close()
    }
    IO.pure(ProcessedFileData(Int.box(count), sensorDataList.toList))
  }

  def parseInt(s: String): Option[Int] = {
    try {
      s.toIntOption match {
        case Some(d) if d.isNaN => None
        case Some(d)            => Some(d)
        case None               => None
      }
    } catch {
      case _: NumberFormatException => None
    }
  }
}
