package com.ubs.tri
package model

case class SensorHumidityReport(
    numberOfFiles: Int,
    numberOfMeasurements: Int,
    numberOfFailedMeasurements: Int,
    sensorHumidityDataList: List[HighestAvgHumidity]
)

case class HighestAvgHumidity(
    sensorId: String,
    min: String,
    avg: String,
    max: String
)
