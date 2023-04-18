package io.laserdisc.scanamo.circe.internal

import io.circe.syntax.*
import io.circe.{Json, *}
import org.scanamo.DynamoValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.jdk.CollectionConverters.*

trait ScanamoWriter[T] {
  def write(t: T): DynamoValue
}

class CirceScanamoWriter[T: Encoder](writeNullObjectAttrs: Boolean) extends ScanamoWriter[T] {

  private val folder = new Json.Folder[AttributeValue] {
    override def onNull: AttributeValue =
      AttributeValue.builder().nul(true).build()

    override def onBoolean(value: Boolean): AttributeValue =
      AttributeValue.builder().bool(value).build()

    override def onNumber(value: JsonNumber): AttributeValue =
      AttributeValue.builder().n(value.toString).build()

    override def onString(value: String): AttributeValue =
      AttributeValue.builder().s(value).build()

    override def onArray(value: Vector[Json]): AttributeValue =
      AttributeValue.builder().l(value.map(j => j.foldWith(this)).asJavaCollection).build()

    override def onObject(value: JsonObject): AttributeValue =
      AttributeValue
        .builder()
        .m(
          value.toMap
            .filter(v => writeNullObjectAttrs || !v._2.isNull)
            .map { case (k, v) => k -> v.foldWith(this) }
            .asJava
        )
        .build()
  }

  override def write(t: T): DynamoValue =
    DynamoValue.fromAttributeValue(
      { if (writeNullObjectAttrs) t.asJson else t.asJson.dropNullValues }.foldWith(folder)
    )
}
