package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class DatasourceRowTests extends FunSuite {

  test("valid parameter") {
    val row = new DatasourceRow(List[DatasourceColumn]())
    assert(row.columns.isEmpty)
  }

  test("null columns") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceRow(null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("columns parameter")))

  }

}
