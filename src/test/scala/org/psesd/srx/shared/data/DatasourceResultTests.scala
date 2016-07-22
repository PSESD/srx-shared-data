package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.scalatest.FunSuite

class DatasourceResultTests extends FunSuite {

  test("valid parameter") {
    val result = new DatasourceResult(true, List[DatasourceRow]())
    assert(result.success)
    assert(result.rows.isEmpty)
  }

  test("null rows") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceResult(true, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("rows parameter")))

  }

}
