package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.config.Environment

object TestValues {

  lazy val datasourceClassName = Environment.getProperty("DATASOURCE_CLASS_NAME")
  lazy val datasourceMaxConnections = Environment.getProperty("DATASOURCE_MAX_CONNECTIONS").toInt
  lazy val datasourceTimeout = Environment.getProperty("DATASOURCE_TIMEOUT").toLong
  lazy val datasourceUrl = Environment.getProperty("DATASOURCE_URL")

  lazy val datasourceConfig = new DatasourceConfig()

}
