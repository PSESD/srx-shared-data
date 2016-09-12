package org.psesd.srx.shared.data

import java.net.URI
import java.sql._
import java.util.UUID

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.psesd.srx.shared.core.sif.SifTimestamp
import org.psesd.srx.shared.data.exceptions.{DatasourceException, DatasourceStatementException}

import scala.collection.mutable.ArrayBuffer

/** Datasource I/O functions.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class Datasource(datasourceConfig: DatasourceConfig) {

  private lazy val dataSource: HikariDataSource = getHikariDataSource(datasourceConfig)

  def close(): Unit = {
    if (!dataSource.isClosed) {
      dataSource.close()
    }
  }

  def create(sql: String, idColumn: String, parameters: Any*): DatasourceResult = {
    if (parameters != null && parameters.nonEmpty) {
      val p = ArrayBuffer[DatasourceParameter]()
      for (i <- parameters.indices) {
        p += DatasourceParameter(parameters(i))
      }
      create(new DatasourceStatement(sql, Some(p.toList)), idColumn)
    } else {
      create(DatasourceStatement(sql), idColumn)
    }
  }

  def create(statement: DatasourceStatement, idColumn: String): DatasourceResult = {
    val connection: Connection = dataSource.getConnection
    try {
      var id: Option[String] = None
      val rows = ArrayBuffer[DataRow]()
      val exceptions = ArrayBuffer[Exception]()
      try {
        val preparedStatement: PreparedStatement = connection.prepareStatement(statement.sql)
        setParameters(preparedStatement, statement.parameters)
        val resultSet: ResultSet = preparedStatement.executeQuery
        val meta: ResultSetMetaData = resultSet.getMetaData
        while (resultSet.next) {
          rows += getRow(resultSet, meta)
        }
        resultSet.close()
        if (rows.nonEmpty && rows.head.columns.nonEmpty) {
          id = Some(rows.head.columns.head.value.toString)
        }
      } catch {
        case e: Exception =>
          exceptions += new DatasourceStatementException(e.getMessage, e)
      }
      new DatasourceResult(id, rows.toList, exceptions.toList)
    } finally {
      if (!connection.isClosed) connection.close()
    }
  }

  def execute(sql: String, parameters: Any*): DatasourceResult = {
    if (parameters != null && parameters.nonEmpty) {
      val p = ArrayBuffer[DatasourceParameter]()
      for (i <- parameters.indices) {
        p += DatasourceParameter(parameters(i))
      }
      execute(new DatasourceStatement(sql, Some(p.toList)))
    } else {
      execute(DatasourceStatement(sql))
    }
  }

  def execute(statement: DatasourceStatement): DatasourceResult = {
    val connection: Connection = dataSource.getConnection
    try {
      var result: Int = 0
      val exceptions = ArrayBuffer[Exception]()
      try {
        val preparedStatement: PreparedStatement = connection.prepareStatement(statement.sql)
        setParameters(preparedStatement, statement.parameters)
        result = preparedStatement.executeUpdate
      } catch {
        case e: Exception =>
          exceptions += new DatasourceStatementException(e.getMessage, e)
      }
      if (result < 0) {
        exceptions += new DatasourceStatementException("Datasource statement returned a result of %s.".format(result.toString), null)
      }
      new DatasourceResult(None, List[DataRow](), exceptions.toList)
    } finally {
      if (!connection.isClosed) connection.close()
    }
  }

  private def setParameters(statement: PreparedStatement, parameters: Option[List[DatasourceParameter]]): Unit = {
    if (parameters.isDefined) {
      for (parameter <- parameters.get) {
        parameter.dataType match {

          case DataType.Date =>
            statement.setDate(parameter.index, parameter.value.asInstanceOf[Date])

          case DataType.Integer =>
            statement.setInt(parameter.index, parameter.value.asInstanceOf[Int])

          case DataType.Null =>
            statement.setNull(parameter.index, Types.OTHER)

          case DataType.String =>
            statement.setString(parameter.index, parameter.value.asInstanceOf[String])

          case DataType.Timestamp =>
            statement.setTimestamp(parameter.index, new Timestamp(parameter.value.asInstanceOf[SifTimestamp].getMilliseconds))

          case DataType.Uuid =>
            statement.setObject(parameter.index, parameter.value.asInstanceOf[UUID])

          case _ =>
        }
      }
    }
  }

  def get(sql: String, parameters: Any*): DatasourceResult = {
    if (parameters != null && parameters.nonEmpty) {
      val p = ArrayBuffer[DatasourceParameter]()
      for (i <- parameters.indices) {
        p += DatasourceParameter(parameters(i))
      }
      get(new DatasourceStatement(sql, Some(p.toList)))
    } else {
      get(DatasourceStatement(sql))
    }
  }

  def get(statement: DatasourceStatement): DatasourceResult = {
    val connection: Connection = dataSource.getConnection
    try {
      val rows = ArrayBuffer[DataRow]()
      val exceptions = ArrayBuffer[Exception]()
      try {
        val preparedStatement: PreparedStatement = connection.prepareStatement(statement.sql)
        setParameters(preparedStatement, statement.parameters)
        val resultSet: ResultSet = preparedStatement.executeQuery
        val meta: ResultSetMetaData = resultSet.getMetaData
        while (resultSet.next) {
          rows += getRow(resultSet, meta)
        }
        resultSet.close()
      } catch {
        case e: Exception =>
          exceptions += new DatasourceStatementException(e.getMessage, e)
      }
      new DatasourceResult(None, rows.toList, exceptions.toList)
    } finally {
      if (!connection.isClosed) connection.close()
    }
  }

  private def getRow(resultSet: ResultSet, meta: ResultSetMetaData): DataRow = {
    val columns = ArrayBuffer[DataColumn]()
    val columnCount = meta.getColumnCount
    for (columnIndex <- 1 to columnCount) {
      val columnName = meta.getColumnName(columnIndex)
      val value: Object = resultSet.getObject(columnName)
      columns += new DataColumn(
        columnIndex,
        columnName,
        {
          if(value == null || resultSet.wasNull()) {
            DataType.Null
          } else {
            DataType.String
          }
        },
        {
          if(value == null || resultSet.wasNull()) {
            null
          } else {
            value.toString
          }
        }
      )
    }
    new DataRow(columns.toList)
  }

  private def getHikariDataSource(datasourceConfig: DatasourceConfig): HikariDataSource = {
    try {
      new HikariDataSource(getHikariConfig(datasourceConfig))
    } catch {

      case de: DatasourceException =>
        throw de

      case re: RuntimeException =>
        val message = re.getMessage
        message match {
          case "java.lang.ClassNotFoundException: invalidClassName" =>
            throw new DatasourceException("Invalid Datasource class name '%s'.".format(datasourceConfig.className), re)

          case "maxPoolSize cannot be less than 1" =>
            throw new DatasourceException("Invalid Datasource max connections '%s'. Cannot be less than 1.".format(datasourceConfig.maxConnections.toString), re)

          case "connectionTimeout cannot be less than 250ms" =>
            throw new DatasourceException("Invalid Datasource timeout '%s'. Cannot be less than 250ms.".format(datasourceConfig.timeout.toString), re)

          case _ =>
            throw new DatasourceException(re.getMessage, re)
        }

      case e: Exception =>
        throw new DatasourceException(e.getMessage, e)
    }
  }

  private def getHikariConfig(datasourceConfig: DatasourceConfig): HikariConfig = {

    val urlProperties = getDatasourceUrlProperties(datasourceConfig.url)

    val result = new HikariConfig
    result.setDataSourceClassName(datasourceConfig.className)
    result.setMaximumPoolSize(datasourceConfig.maxConnections)
    result.setConnectionTimeout(datasourceConfig.timeout)
    result.setValidationTimeout(datasourceConfig.timeout)
    result.setDataSourceProperties(urlProperties)
    result.addDataSourceProperty("ssl", "true")
    result.addDataSourceProperty("sslfactory", "org.postgresql.ssl.NonValidatingFactory")

    result.validate()
    result
  }

  private def getDatasourceUrlProperties(datasourceUrl: String): java.util.Properties = {
    val result = new java.util.Properties()

    try {
      val uri = new URI(datasourceUrl)

      val userInfo = uri.getUserInfo.split(':')

      val map = Map(
        "serverName" -> uri.getHost,
        "portNumber" -> uri.getPort.toString,
        "databaseName" -> uri.getPath.substring(1),
        "user" -> userInfo.head,
        "password" -> userInfo.tail.head
      )

      map.foreach { case (key, value) => result.put(key, value) }
    } catch {
      case e: Exception =>
        throw new DatasourceException("Invalid Datasource URL.", null)
    }

    result
  }

}
