package com.ubs.tri
package service.util

import cats.effect.unsafe.implicits.global
import com.ubs.tri.model.ProcessedFileData
import org.scalatest.funspec.AsyncFunSpec

import java.nio.file.Path

class FileReaderTests extends AsyncFunSpec {
  describe("Unit tests file reader") {
    it("Get csv file reader instance") {
      val dirPath = getClass.getResource("/data").getPath
      val paths = Util.getFilePathsFromFolder(dirPath)
      val fileReader = FileReader(paths)
      assert(fileReader.isDefined)
      assert(fileReader.get.isInstanceOf[CSVFileReader])
    }

    it("Invalid file paths csv instance ") {
      val paths = List.empty[Path]
      val fileReader = FileReader(paths)
      assert(fileReader.isEmpty)
    }

    it("Valid csv instance get the processed data") {
      val dirPath = getClass.getResource("/data").getPath
      val paths = Util.getFilePathsFromFolder(dirPath)
      val fileReader = FileReader(paths)
      val result = fileReader.get.readFileData()
      result.unsafeToFuture().map { pd =>
        assert(pd.isInstanceOf[ProcessedFileData])
        assert(pd.numOfFiles == 2)
        assert(pd.sensorDataList.nonEmpty)
        val sensorIds = pd.sensorDataList.map(_.sensorId)
        assert(sensorIds.contains("s1"))
        assert(sensorIds.contains("s2"))
      }

    }

  }
}
