package io.laserdisc.scanamo.circe

import io.circe.Json
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scanamo.DynamoFormat

trait CommonFormatSpec extends AnyWordSpec with Matchers {
  this: AnyWordSpec =>

  def handleBasicTypes(implicit fmt: DynamoFormat[Json]): Unit = {

    "null" in
      testReadWrite(Json.Null, nullAttr())

    "string" in {
      testReadWrite("\"\"", stringAttr(""))
      testReadWrite("\"hello\"", stringAttr("hello"))
    }

    "number" in {
      testReadWrite("-100", numberAttr("-100"))
      testReadWrite("0", numberAttr("0"))
      testReadWrite("1234", numberAttr("1234"))
      testReadWrite("55.33", numberAttr("55.33"))
    }

    "boolean" in {
      testReadWrite("true", boolAttr(true))
      testReadWrite("false", boolAttr(false))
    }

  }

  def handleArrays(implicit fmt: DynamoFormat[Json]): Unit = {

    "empty array" in testReadWrite("[]", listAttr())

    "basic array" in {
      testReadWrite(
        "[10,20]",
        listAttr(
          numberAttr("10"),
          numberAttr("20")
        )
      )
      testReadWrite(
        "[\"a\",\"b\"]",
        listAttr(
          stringAttr("a"),
          stringAttr("b")
        )
      )
      testReadWrite(
        "[null, null]",
        listAttr(
          nullAttr(),
          nullAttr()
        )
      )
      testReadWrite(
        "[true, false]",
        listAttr(
          boolAttr(true),
          boolAttr(false)
        )
      )
    }

    "mixed array" in testReadWrite(
      "[1,\"a\",true,null,-3]",
      listAttr(
        numberAttr("1"),
        stringAttr("a"),
        boolAttr(true),
        nullAttr(),
        numberAttr("-3")
      )
    )

    "nested array" in testReadWrite(
      """[
        | [1,2],
        | ["a","b"],
        | [null],
        | [true, true],
        | [1, "b"],
        | [[[2, "a"]]]
        |]""".stripMargin,
      listAttr(
        listAttr(numberAttr("1"), numberAttr("2")),
        listAttr(stringAttr("a"), stringAttr("b")),
        listAttr(nullAttr()),
        listAttr(boolAttr(true), boolAttr(true)),
        listAttr(numberAttr("1"), stringAttr("b")),
        listAttr(
          listAttr(
            listAttr(numberAttr("2"), stringAttr("a"))
          )
        )
      )
    )

  }

}
