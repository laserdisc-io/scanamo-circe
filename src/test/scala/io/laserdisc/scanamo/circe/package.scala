package io.laserdisc.scanamo

import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.jdk.CollectionConverters._

package object circe {

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

}
