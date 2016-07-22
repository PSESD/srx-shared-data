package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.psesd.srx.shared.data.exceptions.DatasourceParameterException
import org.scalatest.FunSuite

class DatasourceParameterTests extends FunSuite {

  test("valid parameter") {
    val parameter = new DatasourceParameter(1, "name", DataType.String, "value")
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
        new DatasourceParameter(1, value, DataType.String, "value")
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("name parameter")))
    }
  }

  test("null dataType") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceParameter(1, "name", null, "value")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("dataType parameter")))
  }

  test("null value") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceParameter(1, "name", DataType.Object, null)
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("value parameter")))
  }

  test("invalid integer") {
    val thrown = intercept[DatasourceParameterException] {
      new DatasourceParameter(1, "name", DataType.Integer, "abc")
    }
    assert(thrown.getMessage.equals("Datasource parameter 'name' of type 'integer' contains invalid value 'abc'."))
  }

  test("invalid string") {
    val thrown = intercept[DatasourceParameterException] {
      new DatasourceParameter(1, "name", DataType.String, 123)
    }
    assert(thrown.getMessage.equals("Datasource parameter 'name' of type 'string' contains invalid value '123'."))
  }

  test("invalid timestamp") {
    val thrown = intercept[DatasourceParameterException] {
      new DatasourceParameter(1, "name", DataType.Timestamp, "abc")
    }
    assert(thrown.getMessage.equals("Datasource parameter 'name' of type 'timestamp' contains invalid value 'abc'."))
  }

}
