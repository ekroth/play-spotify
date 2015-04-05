package com.github.ekroth.spotify

trait Caching {
  def saveUser(user: UserAuth): Unit

  def loadUser(oauth: String): Option[UserAuth]

  def saveClient(client: ClientAuth): Unit

  def loadClient(): Option[ClientAuth]
}

trait NoCaching extends Caching {
  def saveUser(user: UserAuth) = ()

  def loadUser(oauth: String) = None

  def saveClient(client: ClientAuth) = ()

  def loadClient() = None
}

trait PlayCacheCaching extends Caching {
  import play.api.Play.current
  import play.api.cache._

  def saveUser(user: UserAuth) = Cache.set(user.oauth, user)

  def loadUser(oauth: String) = Cache.getAs[UserAuth](oauth)

  def saveClient(client: ClientAuth) = ()

  def loadClient() = None
}
