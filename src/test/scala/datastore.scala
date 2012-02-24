package simplengine
package datastore
package test

import org.scalatest.{
  FlatSpec,
  BeforeAndAfter
}
import org.scalatest.matchers.ShouldMatchers

import com.google.appengine.tools.development.testing.{
  LocalDatastoreServiceTestConfig => LocalConfig,
  LocalServiceTestHelper => LocalHelper
}

class DatastoreSpec extends FlatSpec with ShouldMatchers with BeforeAndAfter {

  lazy val helper = new LocalHelper(new LocalConfig())

  before {
    helper.setUp()
  }

  after {
    helper.tearDown()
  }

  import Datastore.service

  object Person extends Kind

  case class Person(firstname: String, lastname: String, age: Int)

  val people = List(
    Person("Philip", "Cali", 26),
    Person("Anna", "Cali", 24)
  )

  "Datastore" should "save persons" in {
    people.foreach { p =>
      val person = Datastore entity Person
      Datastore.save(
        person.set("firstname", p.firstname)
              .set("lastname", p.lastname)
              .set("age", p.age)
      )
    }

    val pquery = Datastore query Person

    val philip = pquery where "age" >== 25
    val anna = pquery where "firstname" is "Anna"
    val all = pquery fetch()

    all.size should be === 2
    philip.fetch().size should be === 1
    anna.fetch().size should be === 1
  }

  "Entities" should "know their kind" in {
    val dude = Datastore entity Person
    Datastore.save(
      dude.set("firstname", "Dude").set("lastname", "Random")
    )

    val query = Datastore query Person where "firstname" is "Dude"
    
    query.one().get.kind should be === Person
  }
}
