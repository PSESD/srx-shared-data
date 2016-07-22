package org.psesd.srx.shared.data

import java.net.URI
import java.sql._

import com.zaxxer.hikari.{HikariConfig, HikariDataSource}
import org.psesd.srx.shared.core.sif.SifTimestamp
import org.psesd.srx.shared.data.exceptions.DatasourceException

import scala.collection.mutable.ArrayBuffer

/** Datasource I/O functions.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class Datasource(datasourceConfig: DatasourceConfig) {

  private lazy val dataSource: HikariDataSource = getHikariDataSource(datasourceConfig)

  def close(): Unit = {
    if (!dataSource.isClosed) {
      dataSource.close()
    }
  }

  def execute(statement: DatasourceStatement): DatasourceResult = {
    val connection: Connection = dataSource.getConnection
    try {
      val preparedStatement: PreparedStatement = connection.prepareStatement(statement.sql)
      setParameters(preparedStatement, statement.parameters)
      val result: Int = preparedStatement.executeUpdate
      result match {
        case 1 => new DatasourceResult(true, List[DatasourceRow]())
        case _ => new DatasourceResult(false, List[DatasourceRow]())
      }
    } finally {
      if (!connection.isClosed) connection.close()
    }
  }

  def get(statement: DatasourceStatement): DatasourceResult = {
    val connection: Connection = dataSource.getConnection
    try {
      val preparedStatement: PreparedStatement = connection.prepareStatement(statement.sql)
      setParameters(preparedStatement, statement.parameters)
      val rows = ArrayBuffer[DatasourceRow]()
      val resultSet: ResultSet = preparedStatement.executeQuery
      val meta: ResultSetMetaData = resultSet.getMetaData
      while (resultSet.next) {
        rows += getRow(resultSet, meta)
      }
      resultSet.close()
      new DatasourceResult(true, rows.toList)
    } finally {
      if (!connection.isClosed) connection.close()
    }
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
        throw new DatasourceException("Invalid Datasource URL.")
    }

    result
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
            throw new DatasourceException("Invalid Datasource class name '%s'.".format(datasourceConfig.className))

          case "maxPoolSize cannot be less than 1" =>
            throw new DatasourceException("Invalid Datasource max connections '%s'. Cannot be less than 1.".format(datasourceConfig.maxConnections.toString))

          case "connectionTimeout cannot be less than 250ms" =>
            throw new DatasourceException("Invalid Datasource timeout '%s'. Cannot be less than 250ms.".format(datasourceConfig.timeout.toString))

          case _ =>
            throw new DatasourceException(re.getMessage)
        }

      case e: Exception =>
        throw new DatasourceException(e.getMessage)
    }
  }

  private def getRow(resultSet: ResultSet, meta: ResultSetMetaData): DatasourceRow = {
    val columns = ArrayBuffer[DatasourceColumn]()
    val columnCount = meta.getColumnCount
    for (columnIndex <- 1 to columnCount) {
      val columnName = meta.getColumnName(columnIndex)
      val value: Object = resultSet.getObject(columnName)
      if (value != null) {
        columns += new DatasourceColumn(
          columnIndex,
          columnName,
          DataType.String,
          value
        )
      }
    }
    new DatasourceRow(columns.toList)
  }

  private def setParameters(statement: PreparedStatement, parameters: List[DatasourceParameter]): Unit = {
    for (parameter <- parameters) {
      parameter.dataType match {
        case DataType.Object =>
          statement.setObject(parameter.index, parameter.value)

        case DataType.String =>
          statement.setString(parameter.index, parameter.value.asInstanceOf[String])

        case DataType.Timestamp =>
          statement.setTimestamp(parameter.index, new Timestamp(parameter.value.asInstanceOf[SifTimestamp].getMilliseconds))

        case _ =>
      }
    }
  }

}
