package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

/** Datasource query/statement to execute.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class DatasourceStatement(val sql: String, val parameters: List[DatasourceParameter]) {
  if (sql.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("sql parameter")
  }
  if (parameters == null) {
    throw new ArgumentNullException("parameters parameter")
  }
}
