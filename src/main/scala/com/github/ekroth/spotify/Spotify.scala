package com.github.ekroth.spotify

trait Spotify extends PlayCommands with Commands with Extensions {
  self: Caching =>
}
