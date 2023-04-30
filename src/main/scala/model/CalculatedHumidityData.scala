package com.ubs.tri
package model

case class CalculatedHumidityData(failedMeasurement: Int, minHumidity: Option[Int], maxHumidity: Option[Int], avgHumidity: Option[Int])
