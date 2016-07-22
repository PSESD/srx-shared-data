package org.psesd.srx.shared.data

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
    val statement = new DatasourceStatement(
      sql, List[DatasourceParameter]()
    )
    val result = datasource.get(statement)
    datasource.close()
    assert(!result.success)
    assert(result.rows.isEmpty)
    assert(result.exceptions.nonEmpty)
  }

  test("test table CRUD operations") {
    val id = 1
    val uniqueId = UUID.randomUUID()
    val createdOn = SifTimestamp()
    val stringValue = "string1"

    val datasource = new Datasource(TestValues.datasourceConfig)

    val createSql = "create table psesd.testTable (id integer, uniqueId uuid, createdOn timestamp, stringValue text);"
    val createStatement = new DatasourceStatement(
      createSql, List[DatasourceParameter]()
    )
    val createResult = datasource.execute(createStatement)
    assert(createResult.success)

    val insertOneSql = "insert into psesd.testTable (id, uniqueId, createdOn, stringValue) values (?, ?, ?, ?);"
    val insertOneStatement = new DatasourceStatement(
      insertOneSql, List[DatasourceParameter](
        new DatasourceParameter(1, "id", DataType.Integer, id),
        new DatasourceParameter(2, "uniqueId", DataType.Object, uniqueId),
        new DatasourceParameter(3, "createdOn", DataType.Timestamp, createdOn),
        new DatasourceParameter(4, "stringValue", DataType.String, stringValue)
      )
    )
    val insertOneResult = datasource.execute(insertOneStatement)
    assert(insertOneResult.success)

    val insertManySql = "insert into psesd.testTable (id, uniqueId, createdOn, stringValue) values (?, ?, ?, ?), (?, ?, ?, ?);"
    val insertManyStatement = new DatasourceStatement(
      insertManySql, List[DatasourceParameter](
        new DatasourceParameter(1, "id", DataType.Integer, 2),
        new DatasourceParameter(2, "uniqueId", DataType.Object, UUID.randomUUID()),
        new DatasourceParameter(3, "createdOn", DataType.Timestamp, SifTimestamp()),
        new DatasourceParameter(4, "stringValue", DataType.String, "string2"),
        new DatasourceParameter(5, "id", DataType.Integer, 3),
        new DatasourceParameter(6, "uniqueId", DataType.Object, UUID.randomUUID()),
        new DatasourceParameter(7, "createdOn", DataType.Timestamp, SifTimestamp()),
        new DatasourceParameter(8, "stringValue", DataType.String, "string3")
      )
    )
    val insertManyResult = datasource.execute(insertManyStatement)
    assert(insertManyResult.success)

    val select1Sql = "select id, uniqueId, createdOn, stringValue from psesd.testTable order by id;"
    val select1Statement = new DatasourceStatement(
      select1Sql, List[DatasourceParameter]()
    )
    val select1Result = datasource.get(select1Statement)
    assert(select1Result.success)
    assert(select1Result.rows.length.equals(3))
    assert(select1Result.rows.head.columns(3).value.equals(stringValue))

    val updateSql = "update psesd.testTable set stringValue = ? where id = 1;"
    val udpateStatement = new DatasourceStatement(
      updateSql, List[DatasourceParameter](
        new DatasourceParameter(1, "stringValue", DataType.String, "string1 UPDATED")
      )
    )
    val updateResult = datasource.execute(udpateStatement)
    assert(updateResult.success)

    val select2Sql = "select stringValue from psesd.testTable where id = ?;"
    val select2Statement = new DatasourceStatement(
      select2Sql, List[DatasourceParameter](
        new DatasourceParameter(1, "id", DataType.Integer, 1)
      )
    )
    val select2Result = datasource.get(select2Statement)
    assert(select2Result.success)
    assert(select2Result.rows.length.equals(1))
    assert(select2Result.rows.head.columns(0).value.equals("string1 UPDATED"))

    val dropSql = "drop table psesd.testTable;"
    val dropStatement = new DatasourceStatement(
      dropSql, List[DatasourceParameter]()
    )
    val dropResult = datasource.execute(dropStatement)
    assert(dropResult.success)

    datasource.close()
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
