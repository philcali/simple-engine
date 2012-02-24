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

  implicit def toEntity(entity: GEntity) = Entity(entity)

  def entity(kind: Kind, key: Option[Key] = None) = {
    key.map(new GEntity(_)).getOrElse(new GEntity(kind.simpleName))
  }

  def save(en: GEntity)(implicit ds: DatastoreService) = ds.put(en)

  def query(kind: Kind) = {
    val q = new Query(kind.simpleName)
    DataDsl(q)
  }
}

