package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.data.DataType.DataType

/** Datasource parameter.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class DatasourceParameter(val index: Int, val name: String, val dataType: DataType, val value: Object) {
  if (name.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("name parameter")
  }
  if (dataType == null) {
    throw new ArgumentNullException("dataType parameter")
  }
  if (value == null) {
    throw new ArgumentNullException("value parameter")
  }
}
