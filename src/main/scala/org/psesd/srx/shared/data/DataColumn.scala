package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.ArgumentNullOrEmptyOrWhitespaceException
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.data.DataType.DataType

/** Datasource field.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class DataColumn(index: Int, name: String, dataType: DataType, value: Object) extends DatasourceParameter(index, name, dataType, value) {
  if(name.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("name parameter")
  }
}
