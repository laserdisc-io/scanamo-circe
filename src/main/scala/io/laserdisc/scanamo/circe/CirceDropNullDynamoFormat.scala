package io.laserdisc.scanamo.circe

import io.circe.*
import io.laserdisc.scanamo.circe.internal.*
import org.scanamo.*

/** Import `CirceNonNullDynamoFormat.*` to get a `DynamoFormat` instance similar to [[CirceDynamoFormat]], but drops null object attributes
  * instead of encoding them as dynamodb objects. <br/><br/> This is useful when you want to use sparse indexes in dynamodb.
  *
  * @see
  *   [[CirceDynamoFormat]] for the default implementation which does not drop null object values
  */
object CirceDropNullDynamoFormat {

  implicit def defaultFormat[T: Encoder: Decoder]: DynamoFormat[T] =
    mkCirceDynamoFormat[T](writeNullObjectAttrs = false)

}
