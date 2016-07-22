package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class DatasourceStatementTests extends FunSuite {

  test("valid statement") {
    val statement = new DatasourceStatement("select * from *", List[DatasourceParameter]())
    assert(statement.sql.equals("select * from *"))
    assert(statement.parameters.isEmpty)
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"$key sql") {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        new DatasourceStatement(value, List[DatasourceParameter]())
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sql parameter")))
    }
  }

  test("null columns") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceStatement("select * from *", null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("parameters parameter")))

  }

}
