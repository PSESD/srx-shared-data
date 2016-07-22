package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.config.Environment
import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Datasource configuration parameters.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class DatasourceConfig(val url: String, val className: String, val maxConnections: Int, val timeout: Long) {
  if (url.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("url parameter")
  }
  if (className.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("className parameter")
  }

  def this() {
    this(
      Environment.getProperty("DATASOURCE_URL"),
      Environment.getProperty("DATASOURCE_CLASS_NAME"),
      Environment.getProperty("DATASOURCE_MAX_CONNECTIONS").toInt,
      Environment.getProperty("DATASOURCE_TIMEOUT").toLong
    )
  }
}
