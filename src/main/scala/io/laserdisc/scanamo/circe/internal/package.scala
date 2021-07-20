package io.laserdisc.scanamo.circe

import io.circe.{ Decoder, Encoder }
import org.scanamo.{ DynamoFormat, DynamoReadError, DynamoValue }

package object internal {

  def mkCirceDynamoFormat[T: Encoder: Decoder](writeNullObjectAttrs: Boolean): DynamoFormat[T] =
    new DynamoFormat[T] {

      override def read(dv: DynamoValue): Either[DynamoReadError, T] =
        new CirceScanamoReader().readAs(dv)

      override def write(t: T): DynamoValue =
        new CirceScanamoWriter(writeNullObjectAttrs).write(t)
    }

}
