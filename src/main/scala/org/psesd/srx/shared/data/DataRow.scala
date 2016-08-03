package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.ArgumentNullException

/** Datasource get result row.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class DataRow(val columns: List[DataColumn]) {
  if (columns == null) {
    throw new ArgumentNullException("columns parameter")
  }
}
