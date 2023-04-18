package io.laserdisc.dynamodb.circe

import cats.syntax.either.*
import io.circe.{Json, JsonObject}
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import java.util.Base64
import scala.jdk.CollectionConverters.*

trait DynamoJsonWriter {
  def write(av: AttributeValue): Either[String, Json]
}

object DynamoJsonWriter {

  def mkWriter: DynamoJsonWriter = new DynamoJsonWriter {

    def write(av: AttributeValue): Either[String, Json] =
      if (av.hasM)
        Json
          .obj(
            av.m()
              .asScala
              .map { case (k, v) =>
                k -> toJson(v)
              }
              .toIndexedSeq*
          )
          .asRight
      else "AttributeValue is not a map".asLeft

    def toJson[T](value: AttributeValue): Json =
      Option(value.s())
        .map(onString)
        .orElse(Option(value.n()).map(onNumber))
        .orElse(Option(value.nul()).map(_ => onNull))
        .orElse(Option(value.bool()).map(b => onBoolean(b)))
        .orElse(Option(value.b()).map(b => onBytes(b)))
        .orElse((if (value.hasM) Option(value.m().asScala.toMap) else None).map(o => onObject(o)))
        .orElse((if (value.hasL) Option(value.l().asScala.toList) else None).map(l => onArray(l)))
        .orElse((if (value.hasSs) Option(value.ss().asScala.toList) else None).map(ss => onStringSet(ss)))
        .orElse((if (value.hasNs) Option(value.ns().asScala.toList) else None).map(ns => onNumberSet(ns)))
        .orElse((if (value.hasBs) Option(value.bs().asScala.toList) else None).map(bs => onBinarySet(bs)))
        // TODO: this is in theory not possible, but it's not clear how to handle it purely
        .getOrElse(
          throw new IllegalArgumentException(s"unexpected type for value $value")
        )

    def onNull: Json = Json.obj("null" -> Json.fromBoolean(true))

    def onBoolean(value: Boolean): Json = Json.obj("bool" -> Json.fromBoolean(value))

    def onNumber(value: String): Json = Json.obj("n" -> Json.fromString(value))

    def onString(value: String): Json = Json.obj("s" -> Json.fromString(value))

    def onBytes(value: SdkBytes): Json =
      Json.obj("b" -> Json.fromString(Base64.getEncoder.encodeToString(value.asByteArray())))

    def onArray(value: List[AttributeValue]): Json = {
      val arr = value.map(toJson)
      Json.obj("l" -> Json.fromValues(arr))
    }

    def onStringSet(value: List[String]): Json = Json.obj("ss" -> Json.fromValues(value.map(Json.fromString)))

    def onNumberSet(value: List[String]): Json = Json.obj("ns" -> Json.fromValues(value.map(Json.fromString)))

    def onBinarySet(value: List[SdkBytes]): Json =
      Json.obj(
        "BS" -> Json.fromValues(value.map(b => Json.fromString(Base64.getEncoder.encodeToString(b.asByteArray()))))
      )

    def onObject(value: Map[String, AttributeValue]): Json = {
      val m = Json.fromJsonObject(
        JsonObject.fromMap(
          value.map { case (k, v) => k -> toJson(v) }
        )
      )
      Json.obj("m" -> m)
    }
  }
}
