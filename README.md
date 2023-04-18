# scanamo-circe

[![Build](https://github.com/laserdisc-io/scanamo-circe/actions/workflows/build.yml/badge.svg)](https://github.com/laserdisc-io/scanamo-circe/actions/workflows/build.yml)
[![Release](https://github.com/laserdisc-io/scanamo-circe/actions/workflows/release.yml/badge.svg)](https://github.com/laserdisc-io/scanamo-circe/actions/workflows/release.yml)
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/scanamo-circe_2.13/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.laserdisc/scanamo-circe_2.13)


This is a small library providing [circe](https://circe.github.io/circe/) implementations
of [Scanamo's](https://www.scanamo.org/) `DynamoFormat`,  converting your models to and from Scanamo 
`AttributeValue`s. 

This allows full use of DynamoDB while allowing arbitrary `Json` objects to be stored 
or reusing existing formats.

## Using

Add the dependency:

```scala
libraryDependencies += "io.laserdisc" %% "scanamo-circe" % "2.0.1"
```

Then import the desired instance wherever you invoke Scanamo operations

```scala
import io.laserdisc.scanamo.circe.CirceDynamoFormat.*
```

*Note:* This imports an implicit `DynamoFormat[T]`, which will expect an implicit circe `Encoder[T]` and `Decoder[T]` in scope.

### CirceDynamoFormat and `null` object values 

`CirceDynamoFormat` is this project's default implementation, which encodes `null` Json values as the equivalent 
dynamodb null representation, i.e. `{ "NULL": true}`. 

This behaviour is undesirable when the attribute in question is in use by a sparse index.  When saving a model
for which the sparse index attribute is  `{ "NULL": true}`, the following error will occur:

```
One or more parameter values were invalid: Type mismatch for Index Key foo Expected: S Actual: NULL
```

In this case, we may wish to simply drop such null attribute values instead of encoding them to the explicit `NULL` type.  
The following import brings an instance which discards all `null` object values during write.

```scala
import io.laserdisc.scanamo.circe.CirceDropNullDynamoFormat
```


### Working with DynamoDB specific JSON

DynamoDB has specific JSON representation for record document.

For example:
```json
{
  "cp6qprBaMzroq7ftjfkmxmYnoa": {
    "n": "-2147483648"
  },
  "uQxfdcJ0v0gTovojbokHt": {
    "bool": false
  },
  "qr3xfwwlcwyhs685ad3ncgsbogzvetnFn8gtcr4yiyda0lwzRymtzwvcTCsj09mc7wqilzKWnwintnsbcf" : {
    "m" : {
      "0ggufbt3lki3wha3Mmyuthnsrcb3rlbjpghorNewoq6aconlrdgVgxftk7bdlSjgsejdSliuUiorkveSwe" : {
        "s" : "sfgzxcoknnygqp5kpcyg0rgeh9vysjihob"
      },
      "pPxhqdlpMcpsyWpuewnqwTbwlxi" : {
        "b" : "AA=="
      },
      "4gafwcniktwdzjbuwbby6nfWqJvnfwe2kpmStrlqLhviYzgldiQ" : {
        "b" : "pnWr/wFQ"
      },
      "ttyXli6sl5Loukx1nhawkvsalpRbql80xNeldygwf0r2nsyTjquwungyrkkhdypenoggvgmAut" : {
        "bool" : false
      },
      "AifaacGqhtulfwvx3n" : {
        "n" : "1146225116"
      }
    }
  },
  "uiHqxaxjgLbAq5eEjza7bzejupdVIlw6faptk4mwemdboxwywhwlgcppopviy": {
    "l": [
      {
        "b": "AYCebCV/f/8uYA=="
      },
      {
        "null": true
      },
      {
        "l": [
          {
            "n": "2147483647"
          },
          {
            "ss": [
              "mbkhzTuijkobrvs9qapKgdcxgs6ymi1gu8cmGCcSUiy3ekbyapdioc",
              "r"
            ]
          },
          {
            "null": true
          }
        ]
      },
      {
        "b": "7/9a"
      }
    ]
  }
}
```

Every field in the above example is annotated with a type.  The following types are supported:
- `n`: number
- `s`: string
- `b`: binary
- `l`: list
- `m`: map
- `null`: null
- `bool`: boolean
- `ss`: set of strings
- `bs`: set of binaries
- `ns`: set of numbers

The top level of the DynamoDB document JSON is always a list of fields in the record. Then every field specified with the type.

First we need to initialize a reader and/or writer.

```scala
import io.laserdisc.dynamodb.circe.{DynamoJsonReader, DynamoJsonWriter}
val reader: DynamoJsonReader = DynamoJsonReader.mkReader
val writer: DynamoJsonWriter = DynamoJsonWriter.mkWriter
```

We can decode the above JSON into a `AttributeValue` like this:

```scala
import io.circe.Json
import io.circe.parser.*
import software.amazon.awssdk.services.dynamodb.model.AttributeValue

val jsonStr =
  """
    |{
    |  "foo": { "n": "21" },
    |  "bar": { "s": "baz" }
    |}
    |""".stripMargin

val dynamoDbModel =for{
  json <- parse(jsonStr)
  av <- reader.read(json)
} yield av

// result is:
// val dynamoDbModel: scala.util.Either[java.io.Serializable,software.amazon.awssdk.services.dynamodb.model.AttributeValue] = Right(AttributeValue(M={bar=AttributeValue(S=baz), foo=AttributeValue(N=21)}))
```

If you need to encode the AttributeValue to JSON, you can use the writer:

```scala
import io.circe.Json
import software.amazon.awssdk.services.dynamodb.model.AttributeValue
// remember the top level has always be `M`
import scala.jdk.CollectionConverters.*
val av = AttributeValue.builder().m(Map(
  "bar" -> AttributeValue.builder().n("42").build(),
  "baz" -> AttributeValue.builder().s("bazz").build()).asJava).build()

val json = writer.write(av)
// result is:
//val json: Either[String,io.circe.Json] =
//Right({
//  "bar" : {
//    "n" : "42"
//  },
//  "baz" : {
//    "s" : "bazz"
//  }
//})
```

##### Where can it be useful?

If you dump DynamoDB table to the S3 bucket ([AWS Data Pipeline](https://docs.aws.amazon.com/datapipeline/latest/DeveloperGuide/dp-importexport-ddb-part2.html)) in DynamoDB JSON format, you can use the reader to read the JSON from the S3 bucket, convert it to an AttributeValue. 

Having the AttributeValue and the codecs between AttributeValue and model case classes ([scanamo](https://www.scanamo.org/) or [dynosaur](https://github.com/SystemFw/dynosaur)) you can do processing of the data in yor domain models directly.

Writing data to DynamoDB using [AWS Data Pipeline](https://docs.aws.amazon.com/datapipeline/latest/DeveloperGuide/dp-importexport-ddb-part2.html) also requires the writer. You can manipulate the data in your domain models and then use the writer to write the data to the S3 and then pipeline it to DynamoDB.