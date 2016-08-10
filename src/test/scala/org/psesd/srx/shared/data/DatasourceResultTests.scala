package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class DatasourceResultTests extends FunSuite {

  test("valid result") {
    val result = new DatasourceResult(None, List[DataRow](), List[Exception]())
    assert(result.success)
    assert(result.id.isEmpty)
    assert(result.rows.isEmpty)
    assert(result.exceptions.isEmpty)
  }

  test("null id") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceResult(null, List[DataRow](), List[Exception]())
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("id parameter")))
  }

  test("null rows") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceResult(None, null, List[Exception]())
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("rows parameter")))
  }

  test("null exceptions") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceResult(None, List[DataRow](), null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("exceptions parameter")))
  }

}
