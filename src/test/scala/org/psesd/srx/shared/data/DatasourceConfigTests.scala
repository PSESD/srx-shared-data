package org.psesd.srx.shared.data

import org.psesd.srx.shared.core.exceptions.{ArgumentNullOrEmptyOrWhitespaceException, ExceptionMessage}
import org.scalatest.FunSuite

class DatasourceConfigTests extends FunSuite {

  test("default config") {
    val config = new DatasourceConfig()
    assert(!config.className.isEmpty)
    assert(config.maxConnections > 0)
    assert(config.timeout > 0)
    assert(!config.url.isEmpty)
  }

  Map("null" -> null,
    "empty" -> "",
    "whitespace" -> "   ").foreach { case (key, value) =>

    test(s"$key url") {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        new DatasourceConfig(value, "className", 1, 1)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("url parameter")))
    }

    test(s"$key className") {
      val thrown = intercept[ArgumentNullOrEmptyOrWhitespaceException] {
        new DatasourceConfig("url", value, 1, 1)
      }
      assert(thrown.getMessage.equals(ExceptionMessage.NotNullOrEmptyOrWhitespace.format("className parameter")))
    }
  }

}
