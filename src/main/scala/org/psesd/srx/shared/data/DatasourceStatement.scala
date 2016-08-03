package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullException, ArgumentNullOrEmptyOrWhitespaceException}
import org.psesd.srx.shared.core.extensions.TypeExtensions._

import scala.collection.mutable.ArrayBuffer

/** Datasource query/statement to execute.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  **/
class DatasourceStatement(val sql: String, val parameters: Option[List[DatasourceParameter]]) {
  if (sql.isNullOrEmpty) {
    throw new ArgumentNullOrEmptyOrWhitespaceException("sql parameter")
  }
  if (parameters == null) {
    throw new ArgumentNullException("parameters parameter")
  }

  if (parameters.isDefined) {
    for (i <- parameters.get.indices) {
      if (parameters.get(i).index == 0) {
        parameters.get(i).index = i + 1
      }
    }
  }

}

object DatasourceStatement {

  def apply(sql: String): DatasourceStatement = new DatasourceStatement(sql, None)

  def apply(sql: String, parameters: Any*): DatasourceStatement = {
    if (parameters == null || parameters.isEmpty) {
      new DatasourceStatement(sql, None)
    } else {
      val p = ArrayBuffer[DatasourceParameter]()
      for (i <- parameters.indices) {
        val value = parameters(i)
        if (value == null) {
          throw new ArgumentNullException("parameter %s".format((i + 1).toString))
        }
        p += DatasourceParameter(parameters(i))
      }
      new DatasourceStatement(sql, Some(p.toList))
    }
  }

}
