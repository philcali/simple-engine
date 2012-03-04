package simplengine
package users

import com.google.appengine.api.users.{
  UserService,
  UserServiceFactory
}

object Login {
  def service = UserServiceFactory.getUserService

  def user = {
    val u = service.getCurrentUser
    if (u != null) Some(u) else None
  }

  def check(fun: UserService => Boolean) = 
    if (fun(service)) Right(user.get) else Left(user)
}
