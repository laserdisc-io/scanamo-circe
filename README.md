# scanamo-circe

This is a small library providing a [circe](https://circe.github.io/circe/) implementation 
of [Scanamo's](https://www.scanamo.org/) `DynamoFormat`,  converting your models to and from Scanamo 
`AttributeValue`. This allows full use of DynamoDB while allowing arbitrary `Json` objects to be stored 
or reusing existing formats.

# Getting started

### Circe

First, add the dependency:

```scala
    libraryDependencies += "io.laserdisc" %% "scanamo-circe" % "2.0.0"
```

Then, import the instance:

```scala
    import io.laserdisc.scanamo.circe.CirceDynamoFormat._
```

This provides a `DynamoFormat[T]`, which will expect an implicit Circe `Encoder` and `Decoder` in scope.

