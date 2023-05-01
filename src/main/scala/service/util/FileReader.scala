package com.ubs.tri
package service.util

import model.{ProcessedFileData, SensorData}

import cats.effect.IO

import java.nio.file.{Files, Path}
import scala.collection.mutable.ListBuffer
import scala.io.{BufferedSource, Source}
import scala.util.{Failure, Success, Try}

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
      parseValidData(sensorDataList, source)
      source.close()
    }
    IO.pure(ProcessedFileData(Int.box(count), sensorDataList.toList))
  }

  private def parseValidData(
      sensorDataList: ListBuffer[SensorData],
      source: BufferedSource
  ) = {
    Try {
      validateCSVFile(source)
    } match {
      case Success(_) =>
        val lines = source.getLines().drop(1)
        for (line <- lines) {
          val Array(sensor_id, humidity) = line.split(",").map(_.trim)
          sensorDataList += SensorData(sensor_id, parseInt(humidity))
        }
      case Failure(exception) => println("Invalid file", exception)
    }
  }

  private def validateCSVFile(source: BufferedSource) = {
    val headerOpt = source.getLines().toList.headOption
    headerOpt match {
      case Some(value) =>
        val headerData = value.split(",").map(_.trim)
        if (
          headerData.length > 2 || !headerData
            .apply(0)
            .equalsIgnoreCase("sensor-id") || !headerData
            .apply(1)
            .equalsIgnoreCase("humidity")
        ) {
          throw new IllegalArgumentException("Invalid csv file headers")
        }
      case None =>
        throw new IllegalArgumentException("No data available")
    }
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
