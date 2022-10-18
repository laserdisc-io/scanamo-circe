package io.laserdisc.scanamo

import io.circe.Json
import io.circe.parser.parse
import org.scalatest.Assertion
import org.scalatest.Assertions.fail
import org.scalatest._
import matchers.should.Matchers._
import org.scanamo.DynamoFormat
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.jdk.CollectionConverters._

package object circe {

  implicit class StringOps(val v: String) extends AnyVal {
    def unsafeToJson: Json = parse(v).getOrElse(fail(s"Couldn't parse  JSON '$v'"))
  }

  def nullAttr(): AttributeValue =
    AttributeValue.builder().nul(true).build()

  def stringAttr(str: String): AttributeValue =
    AttributeValue.builder().s(str).build()

  def numberAttr(str: String): AttributeValue =
    AttributeValue.builder().n(str).build()

  def boolAttr(b: Boolean): AttributeValue =
    AttributeValue.builder().bool(b).build()

  def listAttr(l: AttributeValue*): AttributeValue =
    AttributeValue.builder().l(l.asJava).build()

  def mapAttr(elems: (String, AttributeValue)*): AttributeValue =
    AttributeValue.builder().m(elems.toMap.asJava).build()

  def testReadWrite(jsonString: String, attr: AttributeValue)(
      implicit toTest: DynamoFormat[Json]
  ): Assertion = testReadWrite(jsonString.unsafeToJson, attr)

  def testReadWrite(json: Json, attr: AttributeValue)(
      implicit toTest: DynamoFormat[Json]
  ): Assertion = {
    testWrite(json, attr)
    testRead(attr, json)
  }

  def testWrite(str: String, attr: AttributeValue)(implicit toTest: DynamoFormat[Json]): Assertion =
    testWrite(str.unsafeToJson, attr)

  def testWrite(json: Json, attr: AttributeValue)(implicit toTest: DynamoFormat[Json]): Assertion =
    toTest.write(json).toAttributeValue should equal(attr)

  def testRead(attr: AttributeValue, str: String)(implicit toTest: DynamoFormat[Json]): Assertion =
    testRead(attr, str.unsafeToJson)

  def testRead(attr: AttributeValue, json: Json)(implicit toTest: DynamoFormat[Json]): Assertion =
    toTest.read(attr) should equal(Right(json))

}
