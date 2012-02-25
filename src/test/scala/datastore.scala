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
  val helper = new LocalHelper(new LocalConfig())

  import Datastore.service

  before {
    helper.setUp()

    val people = List(
      Map("firstname" -> "Philip", "lastname" -> "Cali", "age" -> 26),
      Map("firstname" -> "Anna", "lastname" -> "Cali", "age" -> 24)
    )

    people.foreach { p =>
      val person = Datastore entity Person set(
        _.firstname := p("firstname").toString,
        _.lastname := p("lastname").toString,
        _.age := p("age").toString.toInt
      )

      Datastore save person
    }
  }

  after {
    helper.tearDown()
  }

  object Person extends Kind {
    val firstname = Property[String]("firstname")
    val lastname = Property[String]("lastname")
    val age = Property[Long]("age")

    def fullname(person: Entity[Person.type]) = {
      "%s %s".format(person(_.firstname), person(_.lastname))
    }
  }

  "Datastore" should "save persons" in {
    val dude = Datastore entity Person set(
      _.firstname := "Some", _.lastname := "Dude", _.age := 97
    )

    Datastore save dude

    val stored = Datastore get (Person, Person key 3) get

    stored(_.firstname) should be === "Some"
    stored(_.lastname) should be === "Dude"
    stored(_.age) should be === 97

    stored as (_ => "Woot!") should be === "Woot!"
  }

  it should "find all the stored results" in {
    val people = Datastore find Person

    val calis = people where (_.lastname is "Cali")
    val philip = calis and (_.firstname is "Philip")

    calis.fetch().size should be === 2
    philip.fetch().head(_.age) should be === 26
  }

  it should "delete entries by key" in {
    val dudeKey = Person key 1

    Datastore delete dudeKey

    Datastore get (Person, dudeKey) should be === None
  }

  it should "handle hierarchial keys" in {
    val dad = Datastore entity Person set (
      _.firstname := "Dominic", _.lastname := "Cali", _.age := 59
    )

    val dadKey = Datastore save dad

    val brother = Datastore entity (Person, Some(dadKey)) set (
      _.firstname := "Joseph", _.lastname := "Cali", _.age := 28
    )

    Datastore save brother

    brother.parent should be === dadKey
  }
}
