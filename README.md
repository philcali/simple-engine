# Simple Engine for App Engine

Simple Engine is a set of extremely light-weight, low-footprint utilities
designed to wrap sections of the Appengine SDK with a Scala flare.

## Datastore

The datastore was heavily inspired by the [highchair][highchair] datastore,
though there are some notable differences.

__Similarities__:

- Immutable data types
- Type safe

__Differences__:

- Developer only needs to define an Entity kind once
- `highchair` makes heavy use of reflection, `simple-engine` solely uses generics
- `highchair` auto maps property types with implicits which is freaking sweet, `simple-engine` stays far away from implicits

## Usage

Define a `Kind` with properties, and use it with the `Datastore`. That's all.
The library was intended to be used like a DSL.


```scala
import simplengine.datastore.{
  Datastore => DS
  Kind,
  Entity
}
import DS.service

// A Kind has a Self type, which can be used for helper methods
object Person extends Kind {
  val firstname = Property[String]("firstname")
  val lastname= Property[String]("lastname")
  val age = Property[Long]("age")

  // Convenience method for later
  def fullname(person: Entity[Self]) = {
    "%s %s".format(person(_.firstname).get, person(_.lastname).get)
  }
}

// entity creates an entity
val entity = Person entity()

// the set method will set properties on the entity
val philip = entity.set(
  _.firstname := "Philip",
  _.lastname := "Cali",
  _.age := 26
)

// field retrieval
philip(_.firstname).get == "Philip" //true
philip(_.age).get / 13 == 2 // true
philip(_.children) == None // true

// use as for conversion
philip as Person.fullname // "Philip Cali"

// Key generation
val gen = Person key 1

// save will insert or update
val key = Person save philip

// get will retrieve a single entity
Person get key

// delete will remove the record
Person delete key

// queries can compose
val calis = Person where (_.lastname is "Cali")
val calisByAge = calis sort (_.age desc)

// pull with fetch
calis.fetch()

// Limit and offset
calis.fetch(_.limit(10).offset(1))

// Putting it all together
Person where (_.lastname is "Cali") fetch(_.limit(10)) map Person.fullname foreach println
```

## Appengine Integration

All fields are optional by default, and since `simple-engine` is a light
wrapper over the GAE datastore, all fields return the option of what's
expected.

As far as field types, it makes no translation to Scala types. The same
restriction exists on `simple-engine`.

## The Future

The project is currently in _proof of concept_ stage for other things. I don't
know about the future of this project as it currently stands.

[highchair]: https://github.com/chrislewis/highchair
