package org.psesd.srx.shared.data

/** Datasource field data types.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object DataType extends Enumeration {
  type DataType = Value
  val Integer = Value("integer")
  val String = Value("string")
  val Timestamp = Value("timestamp")
  val Uuid = Value("uuid")
}
