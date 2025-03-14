package io.laserdisc.scanamo.circe

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CirceDropNullDynamoFormatSpec extends AnyWordSpec with Matchers with CommonFormatSpec {

  "CirceDropNullDynamoFormat" should {

    import CirceDropNullDynamoFormat.*

    "handle basic types" should
      behave.like(handleBasicTypes)

    "handle arrays" should
      behave.like(handleArrays)

    "handle maps" should {

      "empty map" in testReadWrite("{}", mapAttr())

      "basic map" in {

        val json = """{
                     |  "numberTest" : 3,
                     |  "boolTest" : true,
                     |  "nullTest" : null,
                     |  "stringTest" : "yo"
                     |}""".stripMargin

        testRead(
          mapAttr(
            "stringTest" -> stringAttr("yo"),
            "numberTest" -> numberAttr("3"),
            "nullTest"   -> nullAttr(),
            "boolTest"   -> boolAttr(true)
          ),
          json
        )

        testWrite(
          json,
          mapAttr(
            "stringTest" -> stringAttr("yo"),
            "numberTest" -> numberAttr("3"),
            "boolTest"   -> boolAttr(true)
            // note that "nullTest" is not expected
          )
        )

      }

      "nested map" in {

        val json = """{
                     |  "aaa": [
                     |    ["bbb", 3.09],
                     |    [null, true],
                     |    [{ "ccc": {
                     |        "ddd": [ {
                     |          "eee": [null, ["fff"]],
                     |          "ggg": null
                     |        }]
                     |      },
                     |      "hhh": null
                     |    }]
                     |  ],
                     |  "iii": null
                     |}""".stripMargin

        testRead(
          mapAttr(
            "aaa" -> listAttr(
              listAttr(stringAttr("bbb"), numberAttr("3.09")),
              listAttr(nullAttr(), boolAttr(true)),
              listAttr(
                mapAttr(
                  "ccc" -> mapAttr(
                    "ddd" -> listAttr(
                      mapAttr(
                        "eee" -> listAttr(nullAttr(), listAttr(stringAttr("fff"))),
                        "ggg" -> nullAttr()
                      )
                    )
                  ),
                  "hhh" -> nullAttr()
                )
              )
            ),
            "iii" -> nullAttr()
          ),
          json
        )

        // ggg, hhh and iii should be dropped
        testWrite(
          json,
          mapAttr(
            "aaa" -> listAttr(
              listAttr(stringAttr("bbb"), numberAttr("3.09")),
              listAttr(nullAttr(), boolAttr(true)),
              listAttr(
                mapAttr(
                  "ccc" -> mapAttr(
                    "ddd" -> listAttr(
                      mapAttr("eee" -> listAttr(nullAttr(), listAttr(stringAttr("fff"))))
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

}
