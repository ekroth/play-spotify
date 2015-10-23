/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth
package spotify

trait Caching {
  def saveUser(user: UserAuth): Unit

  def loadUser(oauth: String): Option[UserAuth]

  def saveClient(client: ClientAuth): Unit

  def loadClient(): Option[ClientAuth]
}

trait NoCaching extends Caching {
  override def saveUser(user: UserAuth) = ()

  override def loadUser(oauth: String) = None

  override def saveClient(client: ClientAuth) = ()

  override def loadClient() = None
}
