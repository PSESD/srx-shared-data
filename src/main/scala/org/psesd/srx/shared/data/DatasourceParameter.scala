package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._
import org.psesd.srx.shared.core.sif.SifTimestamp
import org.psesd.srx.shared.data.DataType.DataType
import org.psesd.srx.shared.data.exceptions.DatasourceParameterException

/** Datasource parameter.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class DatasourceParameter(val index: Int, val name: String, val dataType: DataType, val value: Any) {
  if (name.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("name parameter")
  }
  if (dataType == null) {
    throw new ArgumentNullException("dataType parameter")
  }
  if (value == null) {
    throw new ArgumentNullException("value parameter")
  }
  validate()

  private def validate(): Unit = {
    try {
      dataType match {

        case DataType.Integer =>
          value.asInstanceOf[Int]

        case DataType.String =>
          value match {
            case s: String =>
            case _ =>
              throw new DatasourceParameterException("Parameter value is not a String.", null)
          }

        case DataType.Timestamp =>
          value match {
            case s: SifTimestamp =>
            case _ =>
              throw new DatasourceParameterException("Parameter value is not a SifTimestamp.", null)
          }

        case _ =>
      }
    } catch {
      case e: Exception =>
        throw new DatasourceParameterException("Datasource parameter '%s' of type '%s' contains invalid value '%s'.".format(name, dataType.toString, value.toString), e)
    }
  }
}
