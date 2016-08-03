package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class DataRowTests extends FunSuite {

  test("valid parameter") {
    val row = new DataRow(List[DataColumn]())
    assert(row.columns.isEmpty)
  }

  test("null columns") {
    val thrown = intercept[ArgumentNullException] {
      new DataRow(null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("columns parameter")))

  }

}
