package org.psesd.srx.shared.data

import org.scalatest.FunSuite

class ServiceTests extends FunSuite {

  test("service") {
    assert(Service.srxService.service.name.equals("srx-shared-data"))
    assert(Service.srxService.service.version.equals("1.0"))
    assert(Service.srxService.buildComponents.head.name.equals("jdk"))
    assert(Service.srxService.buildComponents.head.version.equals("1.8"))
  }

}
