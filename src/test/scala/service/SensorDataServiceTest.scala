package com.ubs.tri
package service

import com.ubs.tri.service.util.FileReader
import org.scalamock.scalatest.MockFactory
import org.scalatest.funspec.AnyFunSpec

class SensorDataServiceTest extends AnyFunSpec {

  describe("SensorDataService Test") {
    it(
      "When valid process parsed file data, should return generated report of humidity"
    ) {
      val dirPath = "../../resources/data"
      val fileReader = FileReader(dirPath)
      val sensorDataService = SensorDataService(fileReader)
      sensorDataService.generateHumidityReport()
    }

  }

}
