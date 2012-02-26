package simplengine
package datastore

import com.google.appengine.api.datastore.{
  EntityNotFoundException,
  DatastoreService,
  DatastoreServiceFactory,
  Key,
  Entity => GEntity,
  Query => GQuery
}

import util.control.Exception.allCatch

object Datastore {
  implicit val service = DatastoreServiceFactory.getDatastoreService()

  implicit val asnyc = DatastoreServiceFactory.getAsyncDatastoreService()

  def entity[A <: Kind](kind: A, key: Option[Key] = None)(implicit ds: DatastoreService) = {
    val en = key.map(new GEntity(kind.simpleName,_)).getOrElse(new GEntity(kind.simpleName))
    
    new Entity[A](kind, en)
  }

  def get[A <: Kind](kind: A, key: Key)(implicit ds: DatastoreService) = {
    allCatch opt (new Entity[A](kind, ds.get(key)))
  }

  def save(en: Entity[_])(implicit ds: DatastoreService) = ds.put(en.entity)

  def delete(keys: Key*)(implicit ds: DatastoreService) = ds.delete(keys: _*)

  def find[A <: Kind](kind: A) = new Query(kind)
}

