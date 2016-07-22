package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class DatasourceResultTests extends FunSuite {

  test("valid result") {
    val result = new DatasourceResult(List[DatasourceRow](), List[Exception]())
    assert(result.success)
    assert(result.rows.isEmpty)
    assert(result.exceptions.isEmpty)
  }

  test("null rows") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceResult(null, List[Exception]())
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("rows parameter")))
  }

  test("null exceptions") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceResult(List[DatasourceRow](), null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("exceptions parameter")))
  }

}
