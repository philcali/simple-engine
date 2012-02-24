package simplengine
package datastore

import com.google.appengine.api.datastore.{
  Entity => GEntity
}

case class Entity(kind: Kind, entity: GEntity) {
  def key = entity.getKey()

  def parent = entity.getParent()

  def get[A](name: String) = entity.getProperty(name).asInstanceOf[A]

  def set[A](prop: String, value: A) = {
    entity.setProperty(prop, value)
    this
  }

  def has(prop: String) = entity.hasProperty(prop)

  def remove(prop: String) = {
    entity.removeProperty(prop)
    this
  }

  def appId = entity.getAppId

  def namespace = entity.getNamespace
}
