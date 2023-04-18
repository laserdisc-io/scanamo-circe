package io.laserdisc.dynamodb.circe

import org.scalacheck.{Arbitrary, Gen}
import software.amazon.awssdk.core.SdkBytes
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

import scala.jdk.CollectionConverters.*

trait ArbitraryModels {
  implicit def invoiceArb: Arbitrary[AttributeValue] = {
    val stringValue: Gen[AttributeValue] = Gen.alphaNumStr.map(str => AttributeValue.builder().s(str).build())

    val numberValue: Gen[AttributeValue] =
      Gen.chooseNum(Int.MinValue, Int.MaxValue).map(num => AttributeValue.builder().n(num.toString).build())

    val booleanValue: Gen[AttributeValue] =
      Gen.oneOf(true, false).map(bool => AttributeValue.builder().bool(bool).build())

    val binaryValue: Gen[AttributeValue] = Gen
      .choose(1, 10)
      .flatMap(size =>
        Gen
          .listOfN(size, Arbitrary.arbitrary[Byte])
          .map(bytes => AttributeValue.builder().b(SdkBytes.fromByteArray(bytes.toArray)).build())
      )

    val nullValue: Gen[AttributeValue] = Gen.const(AttributeValue.builder().nul(true).build())

    val stringSetValue: Gen[AttributeValue] =
      Gen
        .chooseNum(0, 5)
        .flatMap(size => Gen.listOfN(size, Gen.alphaNumStr).map(strings => AttributeValue.builder().ss(strings*).build()))

    val numberSetValue: Gen[AttributeValue] = Gen
      .chooseNum(0, 5)
      .flatMap(size =>
        Gen
          .listOfN(size, Gen.chooseNum(Int.MinValue, Int.MaxValue))
          .map(nums => AttributeValue.builder().ns(nums.map(_.toString)*).build())
      )

    val binarySetValue =
      Gen
        .chooseNum(0, 5)
        .flatMap(size =>
          Gen
            .listOfN(size, Arbitrary.arbitrary[Byte])
            .map(bytes => AttributeValue.builder().bs(SdkBytes.fromByteArray(bytes.toArray)).build())
        )

    val leafs = Gen
      .oneOf(
        stringValue,
        numberValue,
        booleanValue,
        binaryValue,
        nullValue,
        stringSetValue,
        binarySetValue,
        numberSetValue
      )

    def genList(maxDepth: Int): Gen[AttributeValue] =
      Gen
        .chooseNum(0, 5)
        .flatMap(size =>
          Gen
            .listOfN(
              size,
              Gen.choose(0, maxDepth - 1).flatMap(genTree0)
            )
            .map(elem => AttributeValue.builder().l(elem.asJava).build())
        )

    def genNode(maxDepth: Int): Gen[AttributeValue] =
      Gen
        .chooseNum(0, 5)
        .flatMap(size =>
          Gen
            .listOfN(
              size,
              Gen.zip(
                Gen.alphaNumStr,
                Gen.choose(0, maxDepth - 1).flatMap(genTree0)
              )
            )
            .map(pairs => AttributeValue.builder().m(pairs.toMap.asJava).build())
        )

    def genTree0(maxDepth: Int): Gen[AttributeValue] =
      if (maxDepth == 0) leafs else Gen.oneOf(leafs, genNode(maxDepth), genList(maxDepth))

    Arbitrary(genNode(5))
  }
}
