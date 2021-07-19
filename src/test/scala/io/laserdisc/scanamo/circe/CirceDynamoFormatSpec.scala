package io.laserdisc.scanamo.circe

import io.circe.Json
import io.circe.parser._
import org.scalatest.Assertion
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scanamo.DynamoFormat
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

class CirceDynamoFormatSpec extends AnyWordSpec with Matchers {

  def run(jsonString: String, attr: AttributeValue)(
    implicit toTest: DynamoFormat[Json]
  ): Assertion =
    run(
      parse(jsonString).getOrElse(fail(s"Couldn't parse test JSON '$jsonString'")),
      attr
    )

  def run(json: Json, attr: AttributeValue)(implicit toTest: DynamoFormat[Json]): Assertion = {

    toTest.write(json).toAttributeValue should equal(attr)

    toTest.read(attr) should equal(Right(json))

  }

  "CirceDynamoFormat" should {

    import CirceDynamoFormat._

    "null" in {
      run(Json.Null, nullAttr())
    }

    "string" in {
      run("\"\"", stringAttr(""))
      run("\"hello\"", stringAttr("hello"))
    }

    "number" in {
      run("-100", numberAttr("-100"))
      run("0", numberAttr("0"))
      run("1234", numberAttr("1234"))
      run("55.33", numberAttr("55.33"))
    }

    "boolean" in {
      run("true", boolAttr(true))
      run("false", boolAttr(false))
    }

    "empty array" in run("[]", listAttr())

    "basic array" in {
      run(
        "[10,20]",
        listAttr(
          numberAttr("10"),
          numberAttr("20")
        )
      )
      run(
        "[\"a\",\"b\"]",
        listAttr(
          stringAttr("a"),
          stringAttr("b")
        )
      )
      run(
        "[null, null]",
        listAttr(
          nullAttr(),
          nullAttr()
        )
      )
      run(
        "[true, false]",
        listAttr(
          boolAttr(true),
          boolAttr(false)
        )
      )
    }

    "mixed array" in run(
      "[1,\"a\",true,null,-3]",
      listAttr(
        numberAttr("1"),
        stringAttr("a"),
        boolAttr(true),
        nullAttr(),
        numberAttr("-3")
      )
    )

    "nested array" in run(
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

    "empty map" in run("{}", mapAttr())

    "basic map" in {
      run(
        """{
          |  "numberTest" : 3,
          |  "nullTest" : null,
          |  "boolTest" : true,
          |  "stringTest" : "yo"
          |}""".stripMargin,
        mapAttr(
          "nullTest"   -> nullAttr(),
          "stringTest" -> stringAttr("yo"),
          "numberTest" -> numberAttr("3"),
          "boolTest"   -> boolAttr(true)
        )
      )
    }

    "nested map" in {
      run(
        """{
          | "sub1":{
          |   "sub2": {
          |     "name": "bob",
          |     "city": null
          |   }
          | }
          |}""".stripMargin,
        mapAttr(
          "sub1" -> mapAttr(
            "sub2" -> mapAttr(
              "name" -> stringAttr("bob"),
              "city" -> nullAttr()
            )
          )
        )
      )
    }

    "complex map" in {
      run(
        """{
          |  "aaa": [
          |    ["bbb", 3.09],
          |    [null, true],
          |    [{
          |      "ccc": {
          |        "ddd": [ {
          |          "eee": [9, null, ["fff"]]
          |        }]
          |      }
          |    }]
          |  ]
          |}""".stripMargin,
        mapAttr(
          "aaa" -> listAttr(
            listAttr(stringAttr("bbb"), numberAttr("3.09")),
            listAttr(nullAttr(), boolAttr(true)),
            listAttr(
              mapAttr(
                "ccc" -> mapAttr(
                  "ddd" ->
                    listAttr(
                      mapAttr(
                        "eee" -> listAttr(
                          numberAttr("9"),
                          nullAttr(),
                          listAttr(stringAttr("fff"))
                        )
                      )
                    )
                )
              )
            )
          )
        )
      )
    }

  }

}
