package io.laserdisc.scanamo.circe

import io.circe.*
import io.laserdisc.scanamo.circe.internal.*
import org.scanamo.*

/** Import `CirceDynamoFormat.*` to get the default `DynamoFormat` instance for encoding and decoding models to dynamodb using Circe.
  * <br/><br/>
  *
  * Note: This implementation will encode null Json object attributes as `Null`-type dynamodb objects, e.g. <pre> "foo": { "NULL": true}
  * </pre>
  *
  * @see
  *   [[CirceDropNullDynamoFormat]] for an alternate implementation which drops such null values
  */
object CirceDynamoFormat {

  implicit def defaultFormat[T: Encoder: Decoder]: DynamoFormat[T] =
    mkCirceDynamoFormat[T](writeNullObjectAttrs = true)

}
