package simplengine
package datastore

import com.google.appengine.api.datastore.{
  Entity => GEntity
}

// new Entity(Person) or new Entity(Person, entity)
class Entity[A <: Kind](k: A, en: GEntity) {
  def this(k: A) = this(k, new GEntity(k.simpleName))

  def key = en.getKey()

  def kind = k

  def entity = en.clone()

  def clone(f: GEntity => Unit) = {
    val e = entity
    f(e)
    new Entity[A](k, e)
  }

  def parent = en.getParent()

  def appId = en.getAppId

  def namespace = en.getNamespace

  def set(funs: (A => Entity[A] => Entity[A])*) = {
    funs.foldLeft(this) { (in, fun) => fun(k)(in) }
  }

  def apply[B](fun: (A => PropConversion[B])) = {
    val prop = fun(k)
    prop.fromDatastore(en.getProperty(prop.name))
  }

  def as[B](fun: Entity[A] => B) = fun(this)
}
