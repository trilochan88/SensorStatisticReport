package com.ubs.tri
package service.util

import org.scalatest.funspec.AnyFunSpec

class UtilTest extends AnyFunSpec {
  describe("Util test") {
    it("When folder is valid, fetch file paths") {
      val dirPath = getClass.getResource("/data").getPath
      val paths = Util.getFilePathsFromFolder(dirPath)
      assert(paths.nonEmpty)
      val path = paths.head.getFileName.toString
      assert(path.equalsIgnoreCase("leader-2.csv"))
    }

    it("When folder is invalid, file list should be empty") {
      var dirPath = "dsd"
      val paths = Util.getFilePathsFromFolder(dirPath)
      assert(paths.isEmpty)
    }

  }
}
