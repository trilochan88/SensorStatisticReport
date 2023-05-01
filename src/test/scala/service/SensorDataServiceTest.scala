package com.ubs.tri
package service

import cats.effect.unsafe.implicits.global
import com.ubs.tri.service.util.{FileReader, Util}
import org.scalatest.funspec.{AnyFunSpec, AsyncFunSpec}

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
  }

}
