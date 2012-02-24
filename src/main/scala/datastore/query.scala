package simplengine
package datastore

import com.google.appengine.api.datastore.{
  DatastoreService,
  Entity => GEntity,
  Query,
  FetchOptions
}

import Query.{
  FilterOperator => FO,
  SortDirection => SD
}

import java.lang.{Iterable => Jit}
import collection.JavaConversions._

trait QueryHandler {
  val kind: Kind
  val query: Query
}

case class PropDsl(kind: Kind, prop: String, query: Query) extends QueryHandler {
  def wrap(f: Query => Query) = DataDsl(kind, f(query))

  def is(value: Any) = wrap(_.addFilter(prop, FO.EQUAL, value))
  def not(value: Any) = wrap(_.addFilter(prop, FO.NOT_EQUAL, value))
  def >>(value: Any) = wrap(_.addFilter(prop, FO.GREATER_THAN, value))
  def gt(value: Any) = this.>>(value)
  def >==(value: Any) = wrap(_.addFilter(prop, FO.GREATER_THAN_OR_EQUAL, value))
  def <<(value: Any) = wrap(_.addFilter(prop, FO.LESS_THAN, value))
  def lt(value:Any) = this.<<(value)
  def <==(value: Any) = wrap(_.addFilter(prop, FO.LESS_THAN_OR_EQUAL, value))
  def in(values: Any*) = wrap(_.addFilter(prop, FO.IN, values))
  def asc = wrap(_.addSort(prop, SD.ASCENDING))
  def desc = wrap(_.addSort(prop, SD.DESCENDING))
}

case class DataDsl(kind: Kind, query: Query) extends QueryHandler {
  private def rebuild() = {
    query.getFilterPredicates().foldLeft(new Query) { (in, pred) =>
      in.addFilter(pred.getPropertyName, pred.getOperator, pred.getValue)
    }
  }

  def where(prop: String) = PropDsl(kind, prop, new Query(query.getKind))
  def and(prop: String) = PropDsl(kind, prop, rebuild)
  def sort(prop: String) = PropDsl(kind, prop, rebuild)

  def one()(implicit ds: DatastoreService): Option[Entity] = {
    val entries = this.fetch(s => s)(ds)
    if (entries.isEmpty) None else Some(entries.head)
  }

  def fetch()(implicit ds: DatastoreService): Seq[Entity] = {
    this.fetch(s => s)(ds)
  }

  def fetch(f: FetchOptions => FetchOptions)(implicit ds: DatastoreService) = {
    val fts = FetchOptions.Builder.withDefaults()
    ds.prepare(query).asList(f(fts)).toList.map(Entity(kind, _))
  }
}
