/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth
package spotify

trait Spotify {
  self: Caching =>

  val SpotifyAPI: PlayCommands with Commands with Extensions = new PlayCommands with Commands with Extensions with Caching {
    def saveUser(user: UserAuth): Unit = self.saveUser(user)
    def loadUser(oauth: String): Option[UserAuth] = self.loadUser(oauth)
    def saveClient(client: ClientAuth): Unit = self.saveClient(client)
    def loadClient(): Option[ClientAuth] = self.loadClient()
  }
}
