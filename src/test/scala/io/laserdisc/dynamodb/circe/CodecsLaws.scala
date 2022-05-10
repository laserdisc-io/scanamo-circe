package io.laserdisc.dynamodb.circe

import cats.syntax.either._
import org.scalacheck.Prop.forAll
import org.scalacheck.{Properties, Test}
import org.scalatest.OptionValues
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

object CodecsLaws extends Properties("Codecs") with OptionValues with ArbitraryModels {

  override def overrideParameters(p: Test.Parameters): Test.Parameters =
    p.withMinSuccessfulTests(500).withWorkers(Runtime.getRuntime.availableProcessors)

  val reader: DynamoJsonReader = DynamoJsonReader.mkReader
  val writer: DynamoJsonWriter = DynamoJsonWriter.mkWriter

  property("DynamoDB reader/writer pair is symmetric") = forAll { av: AttributeValue =>
    av == (for {
      w <- writer.write(av)
      r <- reader.read(w)
    } yield r).leftMap(err => s"comparison failed with error $err").toOption.value
  }
}
