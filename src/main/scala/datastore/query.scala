package simplengine
package datastore

import com.google.appengine.api.datastore.{
  FetchOptions
}

import Query.{
  FilterOperator => FO,
  SortDirection => SD
}

import java.lang.{Iterable => Jit}
import collection.JavaConversions._

trait QueryHandler {
  val query: Query
}

case class PropDsl(prop: String, query: Query) extends QueryHandler {
  def wrap(f: Query => Query) = DataDsl(f(query))

  def is(value: Any) = wrap(_.addFilter(prop, FO.EQUAL, value))
  def not(value: Any) = wrap(_.addFilter(prop, FO.NOT_EQUAL, value))
  def >(value: Any) = wrap(_.addFilter(prop, FO.GREATER_THAN, value))
  def >=(value: Any) = wrap(_.addFilter(prop, FO.GREATER_THAN_OR_EQUAL, value))
  def <(value: Any) = wrap(_.addFilter(prop, FO.LESS_THAN, value))
  def <=(value: Any) = wrap(_.addFilter(prop, FO.LESS_THAN_OR_EQUAL, value))
  def in(values: Any*) = wrap(_.addFilter(prop, FO.IN, values))
  def asc = wrap(_.addSort(prop, SD.ASCENDING))
  def desc = wrap(_.addSort(prop, SD.DESCENDING))
}

case class DataDsl(query: Query) extends QueryHandler {
  def where(prop: String) = PropDsl(prop, query)
  def and(prop: String) = PropDsl(prop, query)
  def sort(prop: String) = PropDsl(prop, query)

  def fetch(implicit ds: DatastoreService): Seq[GEntity] = {
    this.fetch(s => s)(ds)
  }

  def fetch(f: FetchOptions => FetchOptions)(implicit ds: DatastoreService) = {
    val fts = FetchOptions.Builder.withDefaults()
    ds.prepare(query).asList(f(fts)).toList
  }
}
