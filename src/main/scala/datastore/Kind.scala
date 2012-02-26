package simplengine
package datastore

import com.google.appengine.api.datastore.{
  Key,
  Entity => GEntity,
  DatastoreService,
  KeyFactory
}

trait PropConversion[A] {
  val name: String

  def toBackend(value: A): Any = value

  def fromDatastore(value: Any) = value.asInstanceOf[A]
}

case class Property[A](name: String) extends PropConversion[A] with PropertyDsl[A] {
  def :=[B <: Kind](value: A) = (e: Entity[B]) => {
    e.clone(_.setProperty(name, toBackend(value)))
  }
}

trait IntegerConversion extends PropConversion[Int] {
  override def fromDatastore(value: Any) = value.toString.toInt
}

trait Kind extends DatastoreIntegration {
  def simpleName = this.getClass.getSimpleName

  def key(id: Long) = KeyFactory.createKey(simpleName, id)

  def parent(key: Key, id: Long) = KeyFactory.createKey(key, simpleName, id)
}

trait DatastoreIntegration { self: Kind =>
  type Self = self.type

  def query = Datastore find self.asInstanceOf[Self]

  def where = query where _

  def get(key: Key)(implicit ds: DatastoreService) =
    Datastore get (self.asInstanceOf[Self], key)

  def entity(key: Option[Key] = None)(implicit ds: DatastoreService) = {
    Datastore entity (self.asInstanceOf[Self], key)
  }

  def save(en: Entity[Self])(implicit ds: DatastoreService) =
    Datastore save en 

  def delete(keys: Key*)(implicit ds: DatastoreService) =
    Datastore delete (keys: _*)
} 
