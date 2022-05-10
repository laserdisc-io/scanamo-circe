package io.laserdisc.dynamodb.circe

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.jdk.CollectionConverters._

class DynamoJsonWriterSpec extends AnyWordSpec with Matchers {

  "write dynamo json" should {

    val av =
      AttributeValue
        .builder()
        .m(
          Map(
            "foo" -> AttributeValue.builder().s("random string").build(),
            "bar" -> AttributeValue.builder().bool(true).build(),
            "zar" -> AttributeValue
              .builder()
              .l(
                AttributeValue.builder().n("12.32").build(),
                AttributeValue.builder().b(SdkBytes.fromUtf8String("FSee==")).build()
              )
              .build()
          ).asJava
        )
        .build()

    val writer = DynamoJsonWriter.mkWriter

    println(writer.write(av))
  }
}
