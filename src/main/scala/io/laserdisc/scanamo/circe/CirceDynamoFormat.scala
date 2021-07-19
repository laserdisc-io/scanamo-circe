package io.laserdisc.scanamo.circe

import io.circe._
import io.laserdisc.scanamo.circe.internal.{ CirceScanamoReader, CirceScanamoWriter }
import org.scanamo._

object CirceDynamoFormat {

  implicit def defaultFormat[T: Encoder: Decoder]: DynamoFormat[T] = new DynamoFormat[T] {

    override def read(dv: DynamoValue): Either[DynamoReadError, T] =
      new CirceScanamoReader().readAs(dv)

    override def write(t: T): DynamoValue =
      new CirceScanamoWriter().write(t)
  }

}
