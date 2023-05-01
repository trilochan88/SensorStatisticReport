package com.ubs.tri
package service

import model.{HighestAvgHumidity, SensorHumidityReport}
import service.util.{FileReader, Util}

import cats.effect.unsafe.implicits.global
import org.scalatest.funspec.AsyncFunSpec

class SensorDataServiceTest extends AsyncFunSpec {

  describe("SensorDataService Test") {
    it(
      "When valid process parsed file data, should return generated report of humidity"
    ) {
      val dirPath = getClass.getResource("/data").getPath
      val fileReader = FileReader(Util.getFilePathsFromFolder(dirPath))
      val sensorDataService = SensorDataService(fileReader)
      sensorDataService.generateHumidityReport().unsafeToFuture().map {
        result =>
          assert(result != null)
          assert(result.numberOfFiles == 2)
          assert(result.numberOfMeasurements == 3)
          assert(result.sensorHumidityDataList.nonEmpty)
          assert(result.numberOfFailedMeasurements == 1)
          val humidityReport =
            result.sensorHumidityDataList.find(_.sensorId == "s1")
          assert(humidityReport.isDefined)
          assert(humidityReport.get.sensorId == "s1")
          assert(humidityReport.get.avg == "36")
          assert(humidityReport.get.min == "10")
          assert(humidityReport.get.max == "98")
      }
    }
    it("When no instance of file reader, throw exception") {
      val fileReader = FileReader(Util.getFilePathsFromFolder(""))
      val sensorDataService = SensorDataService(fileReader)
      val resultFut =
        sensorDataService.generateHumidityReport().unsafeToFuture()
      recoverToSucceededIf[Throwable] {
        resultFut
      }
    }

    it(
      "When invalid csv file used"
    ) {
      val dirPath = getClass.getResource("/invalid_data").getPath
      val fileReader = FileReader(Util.getFilePathsFromFolder(dirPath))
      val sensorDataService = SensorDataService(fileReader)
      val resultFut =
        sensorDataService.generateHumidityReport().unsafeToFuture()
      resultFut.map { result =>
        assert(
          result == SensorHumidityReport(
            1,
            0,
            0,
            List.empty[HighestAvgHumidity]
          )
        )
      }
    }
  }

}
