package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class DataColumnTests extends FunSuite {

  test("valid parameter") {
    val parameter = new DataColumn(1, "name", DataType.String, "value")
    assert(parameter.index.equals(1))
    assert(parameter.name.equals("name"))
    assert(parameter.dataType.equals(DataType.String))
    assert(parameter.value.equals("value"))
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"$key name") {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        new DataColumn(1, value, DataType.String, "value")
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("name parameter")))

    }
  }

  test("null dataType") {
    val thrown = intercept[ArgumentNullException] {
      new DataColumn(1, "name", null, "value")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("dataType parameter")))

  }

  test("null value") {
    val thrown = intercept[ArgumentNullException] {
      new DataColumn(1, "name", DataType.Uuid, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("value parameter")))

  }

}