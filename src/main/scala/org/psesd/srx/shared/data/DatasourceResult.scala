package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.ArgumentNullException

import scala.collection.mutable.ArrayBuffer

/** Datasource execute result.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class DatasourceResult(val success: Boolean, val rows: List[DatasourceRow]) {
  if (rows == null) {
    throw new ArgumentNullException("rows parameter")
  }

  val exceptions = new ArrayBuffer[Exception]()
}
