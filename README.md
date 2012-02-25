# Simple Engine for App Engine

Simple Engine is a set of extremely light-weight, low-footprint utilities
designed to wrap sections of the Appengine SDK with a Scala flare.

## Datastore

The datastore was heavily inspired by the [highchair][highchair] datastore,
though there are some notable differences.

__Similarities__:

- Immutable
- Type safe

__Differences__:

- Developer only needs to define an Entity kind once
- `highchair` makes heavy use of reflection, `simple-engine` solely uses generics

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

object Person extends Kind {
  val firstname = Property[String]("firstname")
  val lastname= Property[String]("lastname")
  val age = Property[Long]("age")

  // Convenience method for later
  def fullname(person: Entity[Person.type]) = {
    "%s %s".format(person(_.firstname), person(_.lastname))
  }
}

// entity creates an entity
val entity = DS entity Person

// the set method will set properties on the entity
val philip = entity.set(
  _.firstname := "Philip",
  _.lastname := "Cali,
  _.age := 26
)

// field retrieval
philip(_.firstname) == "Philip" //true
philip(_.age) / 13 == 2 // true

// use as for conversion
philip as Person.fullname // "Philip Cali"

// Key generation
val gen = Person key 1

// save will insert or update
val key = DS save philip

// get will retrieve a single entity
DS get (Person, key)

// delete will remove the record
DS delete key

// find will initiate the query
val people = DS find Person

// queries can compose
val calis = people where (_.lastname is "Cali")
val calisByAge = calis sort (_.age desc)

// pull with fetch
calis.fetch()

// Limit and offset
calis.fetch(_.limit(10).offset(1))

// Putting it all together
people where (_.lastname is "Cali") fetch(_.limit(10)) map Person.fullname foreach println
```

## Known Issues

The `Int` type does not come over one for one, unfortunately. Should you absolutely
need Integers, then you can make use of the `IntegerConversion` mixin for `Property[Int]`.

I would look something like:

```
val age = new Property[Int]("age") with IntegerConversion // now it'll work!
```

[highchair]: https://github.com/chrislewis/highchair
