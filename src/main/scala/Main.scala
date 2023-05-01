package com.ubs.tri

import service.SensorDataService
import service.util.{FileReader, Util}

import cats.effect.{ExitCode, IO, IOApp}

object Main extends IOApp {
  def run(args: List[String]): IO[ExitCode] = {
    if (args.length != 1) {
      IO {
        println("Sensor data file path is missing")
        ExitCode.Error
      }
    }
    val directoryPath = args.head
    val fileReader = FileReader(Util.getFilePathsFromFolder(directoryPath))
    val sensorDataService = SensorDataService(fileReader)

    sensorDataService.generateHumidityReport().flatMap { stats =>
      IO {
        println(s"Num of processed files: ${stats.numberOfFiles}")
        println(s"Num of processed measurements: ${stats.numberOfMeasurements}")
        println(
          s"Num of failed measurements:${stats.numberOfFailedMeasurements}"
        )
        println(s"Sensors with highest avg humidity:")
        println(s"sensor-id,min,avg,max")
        stats.sensorHumidityDataList.foreach(hmm =>
          println(s"${hmm.sensorId},${hmm.min}, ${hmm.avg},${hmm.max}")
        )

      }.as(ExitCode.Success)

    }

  }

}
