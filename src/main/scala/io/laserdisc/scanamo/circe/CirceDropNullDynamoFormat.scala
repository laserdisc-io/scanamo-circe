package io.laserdisc.scanamo.circe

import io.circe._
import io.laserdisc.scanamo.circe.internal._
import org.scanamo._

/** Import `CirceNonNullDynamoFormat._` to get a `DynamoFormat` instance similar to [[CirceDynamoFormat]], but drops null object attributes
  * instead of encoding them as dynamodb objects. <br/><br/> This is useful when you want to use sparse indexes in dynamodb.
  *
  * @see
  *   [[CirceDynamoFormat]] for the default implementation which does not drop null object values
  */
object CirceDropNullDynamoFormat {

  implicit def defaultFormat[T: Encoder: Decoder]: DynamoFormat[T] =
    mkCirceDynamoFormat[T](writeNullObjectAttrs = false)

}
