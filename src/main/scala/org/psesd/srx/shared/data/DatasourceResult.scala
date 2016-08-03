package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.ArgumentNullException

/** Datasource execute result.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class DatasourceResult(val rows: List[DataRow], val exceptions: List[Exception]) {
  if (rows == null) {
    throw new ArgumentNullException("rows parameter")
  }
  if (exceptions == null) {
    throw new ArgumentNullException("exceptions parameter")
  }

  val success: Boolean = exceptions.isEmpty
}
