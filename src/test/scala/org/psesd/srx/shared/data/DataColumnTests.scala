package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.psesd.srx.shared.data.exceptions.DatasourceParameterException
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

  test("null String") {
    val thrown = intercept[DatasourceParameterException] {
      new DataColumn(1, "name", DataType.String, null)
    }
    assert(thrown.getMessage.equals("Datasource parameter 'name' of type 'string' contains invalid value NULL."))
  }

  test("null value") {
    val parameter = new DataColumn(1, "name", DataType.Null, null)
    assert(parameter.index.equals(1))
    assert(parameter.name.equals("name"))
    assert(parameter.dataType.equals(DataType.Null))
    assert(parameter.value == null)
  }

}
