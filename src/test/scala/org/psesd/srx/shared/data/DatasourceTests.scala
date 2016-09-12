package org.psesd.srx.shared.data

import java.sql.Date
import java.util.UUID

import org.psesd.srx.shared.core.sif.SifTimestamp
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

  test("invalid get") {
    val datasource = new Datasource(TestValues.datasourceConfig)
    val sql = "select foo from bar"
    val result = datasource.get(sql)
    datasource.close()
    assert(!result.success)
    assert(result.rows.isEmpty)
    assert(result.exceptions.nonEmpty)
  }

  test("test table CRUD operations") {
    val id = 1
    val uniqueId = UUID.randomUUID()
    val createdDate = Date.valueOf("2016-01-01")
    val createdOn = SifTimestamp()
    val stringValue = "string1"

    val datasource = new Datasource(TestValues.datasourceConfig)

    val cleanTableResult = datasource.execute("drop table if exists srx_shared_data.testTable;")
    assert(cleanTableResult.success)

    val cleanSchemaResult = datasource.execute("drop schema if exists srx_shared_data;")
    assert(cleanSchemaResult.success)

    val schemaResult = datasource.execute("create schema if not exists srx_shared_data;")
    assert(schemaResult.success)

    val createResult = datasource.execute("create table srx_shared_data.testTable (id integer, uniqueId uuid, createdDate date, createdOn timestamp, stringValue text, nullValue text);")
    assert(createResult.success)

    val insertOneResult = datasource.execute(
      "insert into srx_shared_data.testTable (id, uniqueId, createdDate, createdOn, stringValue, nullValue) values (?, ?, ?, ?, ?, ?);",
      id,
      uniqueId,
      createdDate,
      createdOn,
      stringValue,
      null
    )
    assert(insertOneResult.success)

    val insertManyResult = datasource.execute(
      "insert into srx_shared_data.testTable (id, uniqueId, createdDate, createdOn, stringValue, nullValue) values (?, ?, ?, ?, ?, ?), (?, ?, ?, ?, ?, ?);",
      2,
      UUID.randomUUID(),
      createdOn,
      SifTimestamp(),
      "string2",
      null,
      3,
      UUID.randomUUID(),
      createdOn,
      SifTimestamp(),
      "string3",
      None
    )
    assert(insertManyResult.success)

    val select1Result = datasource.get("select * from srx_shared_data.testTable order by id;")
    assert(select1Result.success)
    assert(select1Result.rows.length.equals(3))
    assert(select1Result.rows.head.getUuid("uniqueId").get.toString.equals(uniqueId.toString))
    assert(select1Result.rows.head.getDate("createdDate").get.toString.equals(createdDate.toString))
    assert(select1Result.rows.head.getTimestamp("createdOn").get.toString.equals(createdOn.toString))
    assert(select1Result.rows.head.getString("stringValue").get.equals(stringValue))
    assert(select1Result.rows.head.getString("nullValue").isEmpty)

    val updateResult = datasource.execute(
      "update srx_shared_data.testTable set stringValue = ? where id = 1;",
      "string1 UPDATED"
    )
    assert(updateResult.success)

    val select2Result = datasource.get(
      "select stringValue from srx_shared_data.testTable where id = ?;",
      1
    )
    assert(select2Result.success)
    assert(select2Result.rows.length.equals(1))
    assert(select2Result.rows.head.getString(1).get.equals("string1 UPDATED"))

    val dropResult = datasource.execute("drop table srx_shared_data.testTable;")
    assert(dropResult.success)

    val dropSchemaResult = datasource.execute("drop schema srx_shared_data;")
    assert(dropSchemaResult.success)

    datasource.close()
  }

}
