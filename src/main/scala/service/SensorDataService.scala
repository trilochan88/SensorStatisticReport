package com.ubs.tri
package service

import model.{
  CalculatedHumidityData,
  HighestAvgHumidity,
  ProcessedFileData,
  SensorHumidityReport
}
import service.util.FileReader

import cats.effect.IO

import scala.collection.mutable.ListBuffer

trait SensorDataService {
  def generateHumidityReport(): IO[SensorHumidityReport]
}
private class SensorDataServiceImpl(fileReaderOpt: Option[FileReader])
    extends SensorDataService {

  private def processFile(): IO[SensorHumidityReport] = {

    fileReaderOpt match {
      case Some(fileReader) =>
        for {
          parsedData <- fileReader.readFileData()
          report <- calculatedHumidityData(parsedData)
        } yield {
          report
        }
      case _ => IO.raiseError(new Throwable("No data exist"))
    }

  }

  private[service] def calculatedHumidityData(
      processFileData: ProcessedFileData
  ) = {
    var failureMeasurementCount = 0
    val groupedData: Map[String, List[Option[Int]]] =
      processFileData.sensorDataList
        .groupBy(_.sensorId)
        .view
        .mapValues(_.map(_.humidity))
        .toMap
    val dataMapGrp: Map[String, CalculatedHumidityData] =
      groupedData.view.mapValues { sd =>
        val failureResultCount = sd.count(_.isEmpty)
        if (failureResultCount == sd.length) {
          failureMeasurementCount += 1
          CalculatedHumidityData(
            failureMeasurementCount,
            Option.empty[Int],
            Option.empty[Int],
            Option.empty[Int]
          )
        } else {
          val validHumidityDataList = sd.flatten
          val minHumidity = validHumidityDataList.min
          val maxHumidity = validHumidityDataList.max
          val avgHumidity = validHumidityDataList.sum / sd.length
          CalculatedHumidityData(
            failureResultCount,
            Some(minHumidity),
            Some(maxHumidity),
            Some(avgHumidity)
          )
        }
      }.toMap
    IO {
      model.SensorHumidityReport(
        processFileData.numOfFiles,
        dataMapGrp.keys.size,
        getFailedMeasurementCount(dataMapGrp),
        getHumidityResult(dataMapGrp).sortBy(_.avg).reverse
      )
    }

  }

  def generateHumidityReport(
  ): IO[SensorHumidityReport] = {
    processFile()
  }

  private def getFailedMeasurementCount(
      dataMap: Map[String, CalculatedHumidityData]
  ): Int = {
    var count = 0
    for (key <- dataMap.keys) {
      dataMap.get(key) match {
        case Some(value) =>
          if (value.failedMeasurement != 0 && value.avgHumidity.isEmpty)
            count += 1
      }
    }
    Int.box(count)
  }

  private def getHumidityResult(
      dataMap: Map[String, CalculatedHumidityData]
  ) = {
    val humidityDataList = new ListBuffer[HighestAvgHumidity]
    for (key <- dataMap.keys) {
      dataMap.get(key) match {
        case Some(data) =>
          humidityDataList += HighestAvgHumidity(
            key,
            data.minHumidity.getOrElse("NaN").toString,
            data.avgHumidity.getOrElse("NaN").toString,
            data.maxHumidity.getOrElse("NaN").toString
          )
      }

    }
    humidityDataList.toList
  }

}
object SensorDataService {
  def apply(fileReader: Option[FileReader]): SensorDataService = {
    new SensorDataServiceImpl(fileReader)
  }
}
