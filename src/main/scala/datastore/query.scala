package simplengine
package datastore

import com.google.appengine.api.datastore.{
  DatastoreService,
  Entity => GEntity,
  Query => GQuery,
  FetchOptions
}

import GQuery.{
  FilterOperator => FO,
  SortDirection => SD
}

import java.lang.{Iterable => Jit}
import collection.JavaConversions._

case class Query[A <: Kind](kind: A, query: GQuery) {
  private def rebuild() = {
    query.getFilterPredicates().foldLeft(new GQuery(kind.simpleName)) { (in, pred) =>
      in.addFilter(pred.getPropertyName, pred.getOperator, pred.getValue)
    }
  }

  private def reorder() = {
    query.getSortPredicates().foldLeft(rebuild) { (in, pred) =>
      in.addSort(pred.getPropertyName, pred.getDirection)
    }
  }

  private def link(q: GQuery, fun: (A => FilterDirective[_])) = {
    val FilterDirective(name, op, value) = fun(kind)
    new Query(kind, q.addFilter(name, op, value))
  }

  def where(fun: (A => FilterDirective[_])) = {
    link(new GQuery(query.getKind), fun)
  }

  def and(fun: (A => FilterDirective[_])) = link(rebuild, fun)

  def sort(fun: A => SortDirective) = {
    val q = reorder()
    val SortDirective(name, direction) = fun(kind)
    new Query(kind, q.addSort(name, direction))
  }

  def fetch()(implicit ds: DatastoreService): Seq[Entity[A]] = {
    this.fetch(s => s)(ds)
  }

  def fetch(f: FetchOptions => FetchOptions)(implicit ds: DatastoreService) = {
    val fts = FetchOptions.Builder.withDefaults()
    ds.prepare(query).asList(f(fts)).toList.map(new Entity(kind, _))
  }
}

case class SortDirective(name: String, direction: SD)

case class FilterDirective[A](name: String, op: FO, value: A*)

trait PropertyTest[A] { self: Property[A] =>

  def is(value: A) = (query: GQuery) =>
    query.addFilter(self.name, FO.EQUAL, value)
}

trait PropertyDsl[A] { self: Property[A] =>
  protected def wrap[A](op: FO, value: A*) =
    FilterDirective(self.name, op, value: _*)

  def is(value: A) = wrap(FO.EQUAL, value)
  def not(value: A) = wrap(FO.NOT_EQUAL, value)
  def >(value: A) = wrap(FO.GREATER_THAN, value)
  def >=(value: A) = wrap(FO.GREATER_THAN_OR_EQUAL, value)
  def <(value: A) = wrap(FO.LESS_THAN, value)
  def <=(value: A) = wrap(FO.LESS_THAN_OR_EQUAL, value)
  def in(values: A*) = wrap(FO.IN, values)

  def asc = SortDirective(self.name, SD.ASCENDING)
  def desc = SortDirective(self.name, SD.DESCENDING)
}
