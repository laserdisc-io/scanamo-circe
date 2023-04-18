package io.laserdisc.dynamodb.circe

import cats.syntax.either.*
import cats.syntax.traverse.*
import io.circe.{Json, JsonNumber, JsonObject}
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import java.util.Base64
import scala.jdk.CollectionConverters.*

trait DynamoJsonReader {
  def read(json: Json): Either[String, AttributeValue]
}

object DynamoJsonReader {

  def mkReader: DynamoJsonReader = new DynamoJsonReader {
    private val folder = new Json.Folder[Either[String, AttributeValue]] {
      private def parseDynamoJsonEntry(key: String, dynamoJson: Json): Either[String, AttributeValue] =
        key.toUpperCase match {
          case "S" =>
            Either
              .fromOption(dynamoJson.asString, s"expected json string, but was $dynamoJson")
              .map(str => AttributeValue.builder().s(str).build())
          case "N" =>
            Either
              .fromOption(dynamoJson.asString, s"expected json string, but was $dynamoJson")
              .map(str => AttributeValue.builder().n(str).build())
          case "B" =>
            Either
              .fromOption(dynamoJson.asString, s"expected json string, but was $dynamoJson")
              .map(str =>
                AttributeValue
                  .builder()
                  .b(SdkBytes.fromByteArray(Base64.getDecoder.decode(str)))
                  .build()
              )
          case "BOOL" =>
            Either
              .fromOption(dynamoJson.asBoolean, s"expected json boolean, but was $dynamoJson")
              .map(bool => AttributeValue.builder().bool(bool).build())
          case "NULL" =>
            Either
              .fromOption(dynamoJson.asBoolean, s"expected json boolean, but was $dynamoJson")
              .map(bool => AttributeValue.builder().nul(bool).build())
          case "SS" =>
            Either
              .fromOption(
                for {
                  arr <- dynamoJson.asArray
                  str <- arr.map(_.asString).sequence
                } yield str,
                s"expected json array of strings, but was $dynamoJson"
              )
              .map(strs => AttributeValue.builder().ss(strs.asJava).build())
          case "NS" =>
            Either
              .fromOption(
                for {
                  arr <- dynamoJson.asArray
                  str <- arr.map(_.asString).sequence
                } yield str,
                s"expected json array of strings, but was $dynamoJson"
              )
              .map(strs => AttributeValue.builder().ns(strs.asJava).build())
          case "BS" =>
            Either
              .fromOption(
                for {
                  arr <- dynamoJson.asArray
                  str <- arr.map(_.asString).sequence
                } yield str,
                s"expected json array of strings, but was $dynamoJson"
              )
              .map(strs =>
                AttributeValue
                  .builder()
                  .bs(strs.map(j => SdkBytes.fromByteArray(Base64.getDecoder.decode(j))).asJava)
                  .build()
              )
          case "L" => dynamoJson.foldWith(this)
          case "M" => dynamoJson.foldWith(this)
          case k   => s"Unknown key: $k".asLeft
        }

      override def onNull: Either[String, AttributeValue] = "not expected".asLeft

      override def onBoolean(value: Boolean): Either[String, AttributeValue] = "not expected".asLeft

      override def onNumber(value: JsonNumber): Either[String, AttributeValue] = "not expected".asLeft

      override def onString(value: String): Either[String, AttributeValue] = "not expected".asLeft

      override def onArray(value: Vector[Json]): Either[String, AttributeValue] =
        value
          .map { v =>
            Either
              .fromOption(v.asObject, s"cannot cast to object: $v")
              .flatMap(o => o.toMap.map { case (k, v) => parseDynamoJsonEntry(k, v) }.head)
          }
          .toList
          .sequence
          .map(l => AttributeValue.builder().l(l.asJava).build())

      override def onObject(value: JsonObject): Either[String, AttributeValue] =
        value.toMap
          .map { case (key, value) =>
            Either
              .fromOption(value.asObject, s"cannot cast to object entry: $key -> $value")
              .flatMap(o => o.toMap.map { case (k, v) => parseDynamoJsonEntry(k, v).map(key -> _) }.head)
          }
          .toList
          .sequence
          .map(l => AttributeValue.builder().m(l.toMap.asJava).build())
    }

    override def read(json: Json): Either[String, AttributeValue] = json.foldWith(folder)
  }
}
