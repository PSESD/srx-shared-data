package org.psesd.srx.shared.data.exceptions

/** Exception for duplicate constraint violations.
  *
  * @version 1.0
  * @since 1.0
  * @author Margarett Ly (iTrellis, LLC)
  **/

class DatasourceDuplicateViolationException(val description: String, val innerException: Exception) extends Exception {
  description
}