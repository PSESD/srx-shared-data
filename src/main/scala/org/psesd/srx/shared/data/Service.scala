package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.{SrxService, SrxServiceComponent}

/** SRX Service definition.
  *
  * @version 1.0
  * @since 1.0
  * @author Stephen Pugmire (iTrellis, LLC)
  * */
object Service {
  val srxService: SrxService = new SrxService(
    new SrxServiceComponent("srx-shared-data", "1.0"),
    List[SrxServiceComponent](
      new SrxServiceComponent("jdk", "1.8")
    )
  )
}
