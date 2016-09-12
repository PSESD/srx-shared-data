package org.psesd.srx.shared.data

import java.sql.Date
import java.util.UUID

import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif.SifTimestamp
import org.psesd.srx.shared.data.DataType.DataType
import org.psesd.srx.shared.data.exceptions.DatasourceParameterException

/** Datasource parameter.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class DatasourceParameter(var index: Int, val name: String, val dataType: DataType, val value: Any) {
  if (dataType == null) {
    throw new ArgumentNullException("dataType parameter")
  }
  validate()

  private def validate(): Unit = {
    try {
      dataType match {

        case DataType.Date =>
          value match {
            case s: Date =>
            case _ =>
              throw new DatasourceParameterException("Parameter value is not a Date.", null)
          }

        case DataType.Integer =>
          value.asInstanceOf[Int]

        case DataType.Null =>
          if(!(value == null || value == None)) {
            throw new DatasourceParameterException("Parameter value is not null.", null)
          }

        case DataType.String =>
          if(value == null || value == None) {
            throw new DatasourceParameterException("Parameter value is not a String.", null)
          } else {
            value match {
              case s: String =>
              case _ =>
                throw new DatasourceParameterException("Parameter value is not a String.", null)
            }
          }

        case DataType.Timestamp =>
          value match {
            case s: SifTimestamp =>
            case _ =>
              throw new DatasourceParameterException("Parameter value is not a SifTimestamp.", null)
          }

        case DataType.Uuid =>
          value match {
            case u: UUID =>
            case _ =>
              throw new DatasourceParameterException("Parameter value is not a UUID.", null)
          }

        case _ =>
      }
    } catch {
      case e: Exception =>
        throw new DatasourceParameterException("Datasource parameter '%s' of type '%s' contains invalid value %s.".format(
          name,
          dataType.toString,
          if(value == null || value == None) {
            "NULL"
          } else {
            "'" + value.toString + "'"
          }
        ), e)
    }
  }
}

object DatasourceParameter {

  def apply(index: Int, name: String, dataType: DataType, value: Any): DatasourceParameter = new DatasourceParameter(index, name, dataType, value)

  def apply(name: String, value: Any): DatasourceParameter = {
    val dataType: DataType = value match {
      case _: Date => DataType.Date
      case _: Int => DataType.Integer
      case _: String => DataType.String
      case _: SifTimestamp => DataType.Timestamp
      case _: UUID => DataType.Uuid
      case _ =>
        throw new DatasourceParameterException("Unknown data type for parameter value '%s'".format(value.toString), null)
    }
    new DatasourceParameter(0, name, dataType, value)
  }

  def apply(value: Any): DatasourceParameter = {
    val dataType: DataType = value match {
      case _: Date => DataType.Date
      case _: Int => DataType.Integer
      case _: String => DataType.String
      case _: SifTimestamp => DataType.Timestamp
      case _: UUID => DataType.Uuid
      case _ =>
        if(value == null || value == None) {
          DataType.Null
        } else {
          throw new DatasourceParameterException("Unknown data type for parameter value '%s'".format(value.toString), null)
        }
    }
    new DatasourceParameter(0, null, dataType, value)
  }

}