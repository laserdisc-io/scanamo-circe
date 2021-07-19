package io.laserdisc.scanamo.circe.internal

import io.circe.{ Json, _ }
import io.circe.syntax._
import org.scanamo.DynamoValue
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.jdk.CollectionConverters._

trait ScanamoWriter[T] {
  def write(t: T): DynamoValue
}

class CirceScanamoWriter[T: Encoder] extends ScanamoWriter[T] {

  private[this] val folder = new Json.Folder[AttributeValue] {
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
        .m(value.toMap.map { case (k, v) => k -> v.foldWith(this) }.asJava)
        .build()
  }

  override def write(t: T): DynamoValue =
    DynamoValue.fromAttributeValue(t.asJson.foldWith(folder))
}
