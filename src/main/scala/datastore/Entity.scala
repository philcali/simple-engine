package simplengine
package datastore

import com.google.appengine.datastore.{
  Entity => GEntity
}

case class Entity(entity: GEntity) {
  def key = entity.getKey()

  def parent = entity.getParent()

  def get[A](name: String) = entity.getProperty(name).asInstanceOf[A]

  def set[A](prop: String, value: A) = {
    entity.setProperty(prop, value)
    entity
  }

  def has(prop: String) = entity.hasProperty(prop)
}
