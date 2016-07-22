package org.psesd.srx.shared.data

import java.util.UUID

import org.psesd.srx.shared.data.exceptions.DatasourceException
import org.scalatest.FunSuite

class DatasourceTests extends FunSuite {

  test("valid datasource") {
    val datasource = new Datasource(TestValues.datasourceConfig)
    datasource.close()
    assert(true)
  }

  test("invalid url") {
    val invalidConfig = new DatasourceConfig(
      "invalidUrl",
      TestValues.datasourceClassName,
      TestValues.datasourceMaxConnections,
      TestValues.datasourceTimeout
    )
    val thrown = intercept[DatasourceException] {
      val datasource = new Datasource(invalidConfig)
      datasource.close()
    }
    assert(thrown.getMessage.equals("Invalid Datasource URL."))
  }

  test("invalid className") {
    val invalidConfig = new DatasourceConfig(
      TestValues.datasourceUrl,
      "invalidClassName",
      TestValues.datasourceMaxConnections,
      TestValues.datasourceTimeout
    )
    val thrown = intercept[DatasourceException] {
      val datasource = new Datasource(invalidConfig)
      datasource.close()
    }
    assert(thrown.getMessage.equals("Invalid Datasource class name 'invalidClassName'."))
  }

  test("invalid maxConnections") {
    val invalidConfig = new DatasourceConfig(
      TestValues.datasourceUrl,
      TestValues.datasourceClassName,
      -99,
      TestValues.datasourceTimeout
    )
    val thrown = intercept[DatasourceException] {
      val datasource = new Datasource(invalidConfig)
      datasource.close()
    }
    assert(thrown.getMessage.equals("Invalid Datasource max connections '-99'. Cannot be less than 1."))
  }

  test("invalid timeout") {
    val invalidConfig = new DatasourceConfig(
      TestValues.datasourceUrl,
      TestValues.datasourceClassName,
      TestValues.datasourceMaxConnections,
      -99
    )
    val thrown = intercept[DatasourceException] {
      val datasource = new Datasource(invalidConfig)
      datasource.close()
    }
    assert(thrown.getMessage.equals("Invalid Datasource timeout '-99'. Cannot be less than 250ms."))
  }

  test("query messages") {
    val datasource = new Datasource(TestValues.datasourceConfig)
    val getAllSql = "select messageid, timestamp, operation, status, source, destination, description, sourceip, useragent, body from psesd.messagetrace where messageid = ?"
    val statement = new DatasourceStatement(
      getAllSql, List[DatasourceParameter](
        new DatasourceParameter(1, "messageid", DataType.Object, UUID.randomUUID())
      )
    )
    val result = datasource.get(statement)
    val rows = result.rows
    datasource.close()
    assert(rows.isEmpty)
  }

}
