package org.psesd.srx.shared.data

import java.sql.Date
import java.util.UUID

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import org.psesd.srx.shared.core.exceptions.ArgumentNullException
import org.psesd.srx.shared.core.sif.SifTimestamp

import scala.util.Try

/** Datasource get result row.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
class DataRow(val columns: List[DataColumn]) {
  if (columns == null) {
    throw new ArgumentNullException("columns parameter")
  }

  def getColumn(index: Int): Option[DataColumn] = {
    columns.find(c => c.index == index)
  }

  def getColumn(name: String): Option[DataColumn] = {
    columns.find(c => c.name.toLowerCase == name.toLowerCase)
  }

  def getBoolean(index: Int): Option[Boolean] = {
    val value = getValue(index)
    if (value.isDefined) {
      Some(Try(value.get.asInstanceOf[String].toBoolean).getOrElse(false))
    } else {
      None
    }
  }

  def getBoolean(name: String): Option[Boolean] = {
    val value = getValue(name)
    if (value.isDefined) {
      Some(Try(value.get.asInstanceOf[String].toBoolean).getOrElse(false))
    } else {
      None
    }
  }

  def getDate(index: Int): Option[Date] = {
    val value = getValue(index)
    if (value.isDefined) {
      Some(Date.valueOf(value.get.asInstanceOf[String]))
    } else {
      None
    }
  }

  def getDate(name: String): Option[Date] = {
    val value = getValue(name)
    if (value.isDefined) {
      Some(Date.valueOf(value.get.asInstanceOf[String]))
    } else {
      None
    }
  }

  def getString(index: Int): Option[String] = {
    val value = getValue(index)
    if (value.isDefined) {
      Some(value.get.asInstanceOf[String])
    } else {
      None
    }
  }

  def getString(name: String): Option[String] = {
    val value = getValue(name)
    if (value.isDefined) {
      Some(value.get.asInstanceOf[String])
    } else {
      None
    }
  }

  def getTimestamp(index: Int): Option[SifTimestamp] = {
    val value = getValue(index)
    if (value.isDefined) {
      Some(SifTimestamp(parseTimestamp(value.get.asInstanceOf[String])))
    } else {
      None
    }
  }

  def getTimestamp(name: String): Option[SifTimestamp] = {
    val value = getValue(name)
    if (value.isDefined) {
      Some(SifTimestamp(parseTimestamp(value.get.asInstanceOf[String])))
    } else {
      None
    }
  }

  def getUuid(index: Int): Option[UUID] = {
    val value = getValue(index)
    if (value.isDefined) {
      Some(UUID.fromString(value.get.asInstanceOf[String]))
    } else {
      None
    }
  }

  def getUuid(name: String): Option[UUID] = {
    val value = getValue(name)
    if (value.isDefined) {
      Some(UUID.fromString(value.get.asInstanceOf[String]))
    } else {
      None
    }
  }

  def getValue(index: Int): Option[Any] = {
    val column = getColumn(index)
    if (column.isDefined && column.get.value != null) {
      Some(column.get.value)
    } else {
      None
    }
  }

  def getValue(name: String): Option[Any] = {
    val column = getColumn(name)
    if (column.isDefined && column.get.value != null) {
      Some(column.get.value)
    } else {
      None
    }
  }

  private def parseTimestamp(value: String): String = {
    DateTime.parse(value, DateTimeFormat.forPattern("yyyy-MM-dd HH:mm:ss.SSS")).toString
  }

}
