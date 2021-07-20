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
import io.laserdisc.scanamo.circe.CirceDynamoFormat._
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
