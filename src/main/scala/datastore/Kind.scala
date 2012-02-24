package simplengine
package datastore

trait Kind {
  def simpleName = this.getClass.getSimpleName
}
