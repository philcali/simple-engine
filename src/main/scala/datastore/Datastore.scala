package simplengine
package datastore

import com.google.appengine.api.datastore.{
  DatastoreService,
  DatastoreServiceFactory,
  Key,
  Entity => GEntity,
  Query
}

object Datastore {
  implicit val service = DatastoreServiceFactory.getDatastoreService()

  implicit val asnyc = DatastoreServiceFactory.getAsyncDatastoreService()

  def entity(kind: Kind, key: Option[Key] = None): Entity = {
    val en = key.map(new GEntity(_)).getOrElse(new GEntity(kind.simpleName))
    Entity(kind, en)
  }

  def save(en: Entity)(implicit ds: DatastoreService) = ds.put(en.entity)

  def query(kind: Kind) = {
    val q = new Query(kind.simpleName)
    DataDsl(kind, q)
  }
}

