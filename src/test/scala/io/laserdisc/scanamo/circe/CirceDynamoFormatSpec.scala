package io.laserdisc.scanamo.circe

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class CirceDynamoFormatSpec extends AnyWordSpec with Matchers with CommonFormatSpec {

  "CirceDynamoFormat" should {

    import CirceDynamoFormat.*

    "handle basic types" should
      behave.like(handleBasicTypes)

    "handle arrays" should
      behave.like(handleArrays)

    "handle maps" should {

      "empty map" in testReadWrite("{}", mapAttr())

      "basic map" in
        testReadWrite(
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

      "nested map" in
        testReadWrite(
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
