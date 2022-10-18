package io.laserdisc.scanamo.circe.internal

import cats.implicits.toBifunctorOps
import io.circe.{Json, _}
import org.scanamo.{DynamoArray, DynamoObject, DynamoReadError, DynamoValue, TypeCoercionError}

import java.io.Serializable

trait ScanamoReader extends Serializable {

  def read(dv: DynamoValue): Json

  def readAs[T: Decoder](dv: DynamoValue): Either[DynamoReadError, T] =
    read(dv).as[T].leftMap(TypeCoercionError)
}

class CirceScanamoReader extends ScanamoReader {

  override def read(dv: DynamoValue): Json = toJson(dv)

  def toJson[T](value: DynamoValue): Json =
    value.asString
      .map(onString)
      .orElse(value.asNumber.map(onNumber))
      .orElse(value.asNull.map(_ => onNull))
      .orElse(value.asBoolean.map(onBoolean))
      .orElse(value.asObject.map(onObject))
      .orElse(value.asArray.map(onArray))
      .getOrElse(
        throw new IllegalArgumentException(s"unexpected type for value $value")
      )

  def onNull: Json = Json.Null

  def onBoolean(value: Boolean): Json =
    Json.fromBoolean(value)

  def onNumber(value: String): Json =
    Json.fromJsonNumber(JsonNumber.fromDecimalStringUnsafe(value))

  def onString(value: String): Json =
    Json.fromString(value)

  def onArray(value: DynamoArray): Json = {
    val arr = value.asArray
      .map(_.map(toJson))
      .orElse(value.asNumericArray.map(_.map(this.onNumber)))
      .orElse(value.asStringArray.map(_.map(this.onString)))
      .getOrElse(List.empty)
    Json.fromValues(arr)
  }

  def onObject(value: DynamoObject): Json =
    Json.fromJsonObject(
      JsonObject.fromMap(
        value.keys
          .zip(value.values)
          .map { case (k, v) => k -> toJson(v) }
          .toMap
      )
    )

}
