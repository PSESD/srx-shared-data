package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class DatasourceStatementTests extends FunSuite {

  test("default constructor") {
    val statement = new DatasourceStatement("select * from *", None)
    assert(statement.sql.equals("select * from *"))
    assert(statement.parameters.isEmpty)
  }

  test("sql-only constructor") {
    val statement = DatasourceStatement("select * from *")
    assert(statement.sql.equals("select * from *"))
    assert(statement.parameters.isEmpty)
  }

  test("n params constructor") {
    val statement = DatasourceStatement("select * from *", "param1", "param2")
    assert(statement.sql.equals("select * from *"))
    assert(statement.parameters.get(0).value.equals("param1"))
    assert(statement.parameters.get(1).value.equals("param2"))
  }

  test("null params constructor") {
    val thrown = intercept[ArgumentNullException] {
      DatasourceStatement("select * from *", null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("parameter 1")))
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"$key sql") {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        DatasourceStatement(value)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("sql parameter")))
    }
  }

  test("null parameters") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceStatement("select * from *", null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("parameters parameter")))
  }

}
