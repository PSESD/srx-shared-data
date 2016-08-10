package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ExceptionMessage}
import org.psesd.srx.shared.data.exceptions.DatasourceParameterException
import org.scalatest.FunSuite

class DatasourceParameterTests extends FunSuite {

  test("default constructor") {
    val parameter = new DatasourceParameter(1, "name", DataType.String, "value")
    assert(parameter.index.equals(1))
    assert(parameter.name.equals("name"))
    assert(parameter.dataType.equals(DataType.String))
    assert(parameter.value.equals("value"))
  }

  test("factory constructor") {
    val parameter = DatasourceParameter(1, "name", DataType.String, "value")
    assert(parameter.index.equals(1))
    assert(parameter.name.equals("name"))
    assert(parameter.dataType.equals(DataType.String))
    assert(parameter.value.equals("value"))
  }

  test("name/value constructor") {
    val parameter = DatasourceParameter("name", "value")
    assert(parameter.index.equals(0))
    assert(parameter.name.equals("name"))
    assert(parameter.dataType.equals(DataType.String))
    assert(parameter.value.equals("value"))
  }

  test("value constructor") {
    val parameter = DatasourceParameter("value")
    assert(parameter.index.equals(0))
    assert(parameter.name == null)
    assert(parameter.dataType.equals(DataType.String))
    assert(parameter.value.equals("value"))
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"$key name") {
      val parameter = new DatasourceParameter(1, value, DataType.String, "value")
      assert(parameter.value.equals("value"))
    }
  }

  test("null dataType") {
    val thrown = intercept[ArgumentNullException] {
      new DatasourceParameter(1, "name", null, "value")
    }
    assert(thrown.getMessage.equals(ExceptionMessage.NotNull.format("dataType parameter")))
  }

  test("null value") {
    val thrown = intercept[DatasourceParameterException] {
      new DatasourceParameter(1, "name", DataType.Uuid, null)
    }
    assert(thrown.getMessage.equals("Datasource parameter 'name' of type 'uuid' contains invalid value NULL."))
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
