package org.psesd.srx.shared.data.exceptions

/** Exception for datasource parameter values.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class DatasourceParameterException(val description: String, val innerException: Exception) extends Exception(
  description
)
