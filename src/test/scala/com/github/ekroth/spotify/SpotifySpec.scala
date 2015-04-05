package com.github.ekroth.spotify

import org.scalatest._

import play.api.libs.json._

import scala.reflect.ClassTag

class SpotifySpec extends FlatSpec with Matchers with Spotify {
  case class Foo(name: String, tipe: String)

  behavior of "Spotify"

  def inOut[T : Writes : Reads : ClassTag](js: String, expected: T, name: Option[String] = None) = {
    val className = implicitly[ClassTag[T]]
    it should s"Write/Read '${name.getOrElse(className.toString)}'" in {
      val jsoned = Json.toJson(expected)
      val parsed = Json.parse(js)

      val writtenObj: JsObject = jsoned.asInstanceOf[JsObject]
      val parsedObj: JsObject = parsed.asInstanceOf[JsObject]

      writtenObj.keys should be (parsedObj.keys.filter { parsedObj.value(_) != JsNull })

      jsoned.validate[T] should be (JsSuccess(expected))
      parsed.validate[T] should be (JsSuccess(expected))
    }
  }

  /* album object (full) */
  inOut(SpotifyExamples.AlbumFull, AlbumFull(
    "album",
    Seq(
      ArtistSimplified(
        Map("spotify" -> "https://open.spotify.com/artist/2BTZIqw0ntH9MvilQ3ewNY"),
        "https://api.spotify.com/v1/artists/2BTZIqw0ntH9MvilQ3ewNY",
        "2BTZIqw0ntH9MvilQ3ewNY",
        "Cyndi Lauper",
        "artist",
        "spotify:artist:2BTZIqw0ntH9MvilQ3ewNY")
    ),
    Seq("AD"),
    Seq(Copyright("(P) 2000 Sony Music Entertainment Inc.", "P")),
    Map("upc" -> "5099749994324"),
    Map("spotify" -> "https://open.spotify.com/album/0sNOF9WDwhWunNAHPD3Baj"),
    Seq.empty,
    "https://api.spotify.com/v1/albums/0sNOF9WDwhWunNAHPD3Baj",
    "0sNOF9WDwhWunNAHPD3Baj",
    Seq(
      Image(Some(640), "https://i.scdn.co/image/07c323340e03e25a8e5dd5b9a8ec72b69c50089d", Some(640)),
      Image(Some(300), "https://i.scdn.co/image/8b662d81966a0ec40dc10563807696a8479cd48b", Some(300)),
      Image(Some(64), "https://i.scdn.co/image/54b3222c8aaa77890d1ac37b3aaaa1fc9ba630ae", Some(64))
    ),
    "She's So Unusual",
    39,
    "1983",
    "year",
    Paging[TrackSimplified](
      "https://api.spotify.com/v1/albums/0sNOF9WDwhWunNAHPD3Baj/tracks?offset=0&limit=50",
      Seq(TrackSimplified(
        Seq.empty,
        Seq("AD"),
        1,
        305560,
        false,
        Map("spotify" -> "https://open.spotify.com/track/3f9zqUnrnIq0LANhmnaF0V"),
        "https://api.spotify.com/v1/tracks/3f9zqUnrnIq0LANhmnaF0V",
        "3f9zqUnrnIq0LANhmnaF0V",
        None,
        None,
        "Money Changes Everything",
        "https://p.scdn.co/mp3-preview/01bb2a6c9a89c05a4300aea427241b1719a26b06",
        1,
        "track",
        "spotify:track:3f9zqUnrnIq0LANhmnaF0V")),
      50,
      None,
      0,
      None,
      13),
    "album",
    "spotify:album:0sNOF9WDwhWunNAHPD3Baj"))

  /* album object (simplified) */
  inOut(SpotifyExamples.AlbumSimplified, AlbumSimplified(
    "album",
    Seq("AD", "AR"),
    Map("spotify" -> "https://open.spotify.com/album/34EYk8vvJHCUlNrpGxepea"),
    "https://api.spotify.com/v1/albums/34EYk8vvJHCUlNrpGxepea",
    "34EYk8vvJHCUlNrpGxepea",
    Seq(
      Image(Some(640), "https://i.scdn.co/image/6324fe377dcedf110025527873dafc9b7ee0bb34", Some(640)),
      Image(Some(300), "https://i.scdn.co/image/d2e2148023e8a87b7a2f8d2abdfa936154e470b8", Some(300))),
    "Elvis 75 - Good Rockin' Tonight",
    "album",
    "spotify:album:34EYk8vvJHCUlNrpGxepea"))

  /* artist object (full) */
  inOut(SpotifyExamples.ArtistFull, ArtistFull(
    Map("spotify" -> "https://open.spotify.com/artist/0OdUWJ0sBjDrqHygGUXeCF"),
    Followers(None, 306565),
    Seq("indie folk", "indie pop"),
    "https://api.spotify.com/v1/artists/0OdUWJ0sBjDrqHygGUXeCF",
    "0OdUWJ0sBjDrqHygGUXeCF",
    Seq.empty,
    "Band of Horses",
    59,
    "artist",
    "spotify:artist:0OdUWJ0sBjDrqHygGUXeCF"
  ))

  /* artist object (simplified) */
  inOut(SpotifyExamples.ArtistSimplified, ArtistSimplified(
    Map("spotify" -> "https://open.spotify.com/artist/43ZHCT0cAZBISjO8DG9PnE"),
    "https://api.spotify.com/v1/artists/43ZHCT0cAZBISjO8DG9PnE",
    "43ZHCT0cAZBISjO8DG9PnE",
    "Elvis Presley",
    "artist",
    "spotify:artist:43ZHCT0cAZBISjO8DG9PnE"))

  /* category object */
  inOut(SpotifyExamples.Category, Category("https://api.spotify.com/v1/browse/categories/party",List(Image(Some(274), "https://datsnxq1rwndn.cloudfront.net/media/derived/party-274x274_73d1907a7371c3bb96a288390a96ee27_0_0_274_274.jpg", Some(274))), "party", "Party"))

  /* copyright object */
  inOut(SpotifyExamples.Copyright, Copyright("(C) 2013 Universal Island Records, a division of Universal Music Operations Limited", "C"))

  /* error object */
  inOut(SpotifyExamples.Error, Error(202, "this is bad"))

  /* external ID object */
  inOut(SpotifyExamples.ExternalID, Map("isrc" -> "USR", "kalo" -> "POP"), Some("spotify.Objects$ExternalID"))

  /* external URL object */
  inOut(SpotifyExamples.ExternalURL, Map("spotify" -> "https://url", "spowtify" -> "https://orl"), Some("spotify.Objects$ExternalURL"))

  /* followers object */
  inOut(SpotifyExamples.Followers, Followers(None, 4561))

  /* image object */
  inOut(SpotifyExamples.Image, Image(None, "http://profile-images.scdn.co/artists/default/d4f208d4d49c6f3e1363765597d10c4277f5b74f", None))

  /* paging object */
  inOut(SpotifyExamples.PagingEmpty, Paging[PlaylistSimplified](
    "https://api.spotify.com/v1/browse/categories/party/playlists?country=BR&offset=0&limit=20",
    Seq.empty,
    20,
    Some("https://api.spotify.com/v1/browse/categories/party/playlists?country=BR&offset=20&limit=20"),
    0,
    None,
    148), Some("PagingEmpty"))
  inOut(SpotifyExamples.PagingArrayTrackSimplified, Paging[TrackSimplified](
    "https://api.spotify.com/v1/albums/0sNOF9WDwhWunNAHPD3Baj/tracks?offset=0&limit=50",
    Seq(TrackSimplified(
      Seq.empty,
      Seq("AD"),
      1,
      305560,
      false,
      Map("spotify" -> "https://open.spotify.com/track/3f9zqUnrnIq0LANhmnaF0V"),
      "https://api.spotify.com/v1/tracks/3f9zqUnrnIq0LANhmnaF0V",
      "3f9zqUnrnIq0LANhmnaF0V",
      None,
      None,
      "Money Changes Everything",
      "https://p.scdn.co/mp3-preview/01bb2a6c9a89c05a4300aea427241b1719a26b06",
      1,
      "track",
      "spotify:track:3f9zqUnrnIq0LANhmnaF0V")),
    50,
    None,
    0,
    None,
    13
  ), Some("PagingArrayTrackSimplified"))

  /* playlist object (full) */
  inOut(SpotifyExamples.PlaylistFull, PlaylistFull(
    false,
    Some("Having friends over for dinner? Here´s the perfect playlist."),
    Map("spotify" -> "http://open.spotify.com/user/spotify/playlist/59ZbFPES4DQwEjBpWHzrtC"),
    Followers(None, 143350),
    "https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC",
    "59ZbFPES4DQwEjBpWHzrtC",
    Seq.empty,
    "Dinner with Friends",
    UserPublicOther(
      Map("spotify" -> "http://open.spotify.com/user/spotify"),
      "https://api.spotify.com/v1/users/spotify",
      "spotify",
      "user",
      "spotify:user:spotify"),
    None,
    "bNLWdmhh+HDsbHzhckXeDC0uyKyg4FjPI/KEsKjAE526usnz2LxwgyBoMShVL+z+",
    Paging[PlaylistTrack](
      "https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC/tracks",
      Seq.empty,
      100,
      Some("https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC/tracks?offset=100&limit=100"),
      0,
      None,
      105),
    "playlist",
    "spotify:user:spotify:playlist:59ZbFPES4DQwEjBpWHzrtC"))

  /* tracks object */
  inOut(SpotifyExamples.Tracks, Tracks(
    "https://api.spotify.com/v1/users/spotifybrazilian/playlists/4k7EZPI3uKMz4aRRrLVfen/tracks",
    80))

  /* playlist object (simplified) */
  inOut(SpotifyExamples.PlaylistSimplified, PlaylistSimplified(
    false,
    Map("spotify" -> "http://open.spotify.com/user/spotifybrazilian/playlist/4k7EZPI3uKMz4aRRrLVfen"),
    "https://api.spotify.com/v1/users/spotifybrazilian/playlists/4k7EZPI3uKMz4aRRrLVfen",
    "4k7EZPI3uKMz4aRRrLVfen",
    Seq(Image(Some(300), "https://i.scdn.co/image/bf6544c213532e9650088dfef76c8521093d970e", Some(300))),
    "Noite Eletrônica",
    UserPublic(
      "Lilla Namo",
      Map("spotify" -> "https://open.spotify.com/user/tuggareutangranser"),
      Followers(None, 4561),
      "https://api.spotify.com/v1/users/tuggareutangranser",
      "tuggareutangranser",
      Seq(Image(None, "http://profile-images.scdn.co/artists/default/d4f208d4d49c6f3e1363765597d10c4277f5b74f", None)),
      "user",
      "spotify:user:tuggareutangranser"),
    None,
    Tracks("https://api.spotify.com/v1/users/spotifybrazilian/playlists/4k7EZPI3uKMz4aRRrLVfen/tracks", 80),
    "playlist",
    "spotify:user:spotifybrazilian:playlist:4k7EZPI3uKMz4aRRrLVfen"
  ))

  /* playlist track object */
  inOut(SpotifyExamples.PlaylistTrack, PlaylistTrack(
    Some("2014-09-01T04:21:28Z"),
    UserPublicOther(
      Map.empty,
      "https://api.spotify.com/v1/users/spotify",
      "spotify",
      "user",
      "spotify:user:spotify"),
    TrackFull(
      AlbumSimplified(
        "album",
        Seq("AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY"),
        Map("spotify" -> "https://open.spotify.com/album/34EYk8vvJHCUlNrpGxepea"),
        "https://api.spotify.com/v1/albums/34EYk8vvJHCUlNrpGxepea",
        "34EYk8vvJHCUlNrpGxepea",
        Seq(),
        "Elvis 75 - Good Rockin' Tonight",
        "album",
        "spotify:album:34EYk8vvJHCUlNrpGxepea"),
      Seq(
        ArtistSimplified(
          Map("spotify" -> "https://open.spotify.com/artist/43ZHCT0cAZBISjO8DG9PnE"),
          "https://api.spotify.com/v1/artists/43ZHCT0cAZBISjO8DG9PnE",
          "43ZHCT0cAZBISjO8DG9PnE",
          "Elvis Presley",
          "artist",
          "spotify:artist:43ZHCT0cAZBISjO8DG9PnE")),
      Seq("AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY"),
      3,
      260973,
      false,
      Map("isrc" -> "USRC16901355"),
      Map("spotify" -> "https://open.spotify.com/track/6fgjU6IfBOXHI3OKtndEeE"),
      "https://api.spotify.com/v1/tracks/6fgjU6IfBOXHI3OKtndEeE",
      "6fgjU6IfBOXHI3OKtndEeE",
      None,
      None,
      "Suspicious Minds",
      70,
      Some("https://p.scdn.co/mp3-preview/3742af306537513a4f446d7c8f9cdb1cea6e36d1"),
      19,
      "track",
      "spotify:track:6fgjU6IfBOXHI3OKtndEeE")))

  /* saved track object */
  inOut(SpotifyExamples.SavedTrack, SavedTrack(
    "2014-07-08T14:05:27Z",
    TrackFull(
      AlbumSimplified(
        "album",
        Seq("AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY"),
        Map("spotify" -> "https://open.spotify.com/album/34EYk8vvJHCUlNrpGxepea"),
        "https://api.spotify.com/v1/albums/34EYk8vvJHCUlNrpGxepea",
        "34EYk8vvJHCUlNrpGxepea",
        Seq(),
        "Elvis 75 - Good Rockin' Tonight",
        "album",
        "spotify:album:34EYk8vvJHCUlNrpGxepea"),
      Seq(
        ArtistSimplified(
          Map("spotify" -> "https://open.spotify.com/artist/43ZHCT0cAZBISjO8DG9PnE"),
          "https://api.spotify.com/v1/artists/43ZHCT0cAZBISjO8DG9PnE",
          "43ZHCT0cAZBISjO8DG9PnE",
          "Elvis Presley",
          "artist",
          "spotify:artist:43ZHCT0cAZBISjO8DG9PnE")),
      Seq("AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY"),
      3,
      260973,
      false,
      Map("isrc" -> "USRC16901355"),
      Map("spotify" -> "https://open.spotify.com/track/6fgjU6IfBOXHI3OKtndEeE"),
      "https://api.spotify.com/v1/tracks/6fgjU6IfBOXHI3OKtndEeE",
      "6fgjU6IfBOXHI3OKtndEeE",
      None,
      None,
      "Suspicious Minds",
      70,
      Some("https://p.scdn.co/mp3-preview/3742af306537513a4f446d7c8f9cdb1cea6e36d1"),
      19,
      "track",
      "spotify:track:6fgjU6IfBOXHI3OKtndEeE")))

  /* track object (full) */
  inOut(SpotifyExamples.TrackFull, TrackFull(
    AlbumSimplified(
      "album",
      Seq("AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY"),
      Map("spotify" -> "https://open.spotify.com/album/34EYk8vvJHCUlNrpGxepea"),
      "https://api.spotify.com/v1/albums/34EYk8vvJHCUlNrpGxepea",
      "34EYk8vvJHCUlNrpGxepea",
      Seq(),
      "Elvis 75 - Good Rockin' Tonight",
      "album",
      "spotify:album:34EYk8vvJHCUlNrpGxepea"),
    Seq(
      ArtistSimplified(
        Map("spotify" -> "https://open.spotify.com/artist/43ZHCT0cAZBISjO8DG9PnE"),
        "https://api.spotify.com/v1/artists/43ZHCT0cAZBISjO8DG9PnE",
        "43ZHCT0cAZBISjO8DG9PnE",
        "Elvis Presley",
        "artist",
        "spotify:artist:43ZHCT0cAZBISjO8DG9PnE")),
    Seq("AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY"),
    3,
    260973,
    false,
    Map("isrc" -> "USRC16901355"),
    Map("spotify" -> "https://open.spotify.com/track/6fgjU6IfBOXHI3OKtndEeE"),
    "https://api.spotify.com/v1/tracks/6fgjU6IfBOXHI3OKtndEeE",
    "6fgjU6IfBOXHI3OKtndEeE",
    None,
    None,
    "Suspicious Minds",
    70,
    Some("https://p.scdn.co/mp3-preview/3742af306537513a4f446d7c8f9cdb1cea6e36d1"),
    19,
    "track",
    "spotify:track:6fgjU6IfBOXHI3OKtndEeE"))

  /* track object (simplified) */
  inOut(SpotifyExamples.TrackSimplified, TrackSimplified(
    Seq(
      ArtistSimplified(
        Map("spotify" -> "https://open.spotify.com/artist/08td7MxkoHQkXnWAYD8d6Q"),
        "https://api.spotify.com/v1/artists/08td7MxkoHQkXnWAYD8d6Q",
        "08td7MxkoHQkXnWAYD8d6Q",
        "Tania Bowra",
        "artist",
        "spotify:artist:08td7MxkoHQkXnWAYD8d6Q")
    ),
    Seq("AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY"),
    1,
    276773,
    false,
    Map("spotify" -> "https://open.spotify.com/track/2TpxZ7JUBn3uw46aR7qd6V"),
    "https://api.spotify.com/v1/tracks/2TpxZ7JUBn3uw46aR7qd6V",
    "2TpxZ7JUBn3uw46aR7qd6V",
    None,
    None,
    "All I Want",
    "https://p.scdn.co/mp3-preview/6d00206e32194d15df329d4770e4fa1f2ced3f57",
    1,
    "track",
    "spotify:track:2TpxZ7JUBn3uw46aR7qd6V"))

  /* track link */
  inOut(SpotifyExamples.TrackLink, TrackLink(Map("spotify" -> "https://url", "spowtify" -> "https://orl"), "localhost", "123", "track" , "spotify://spot"))

  /* user object (private) */
  inOut(SpotifyExamples.UserPrivate, UserPrivate(
    Some("1937-06-01"),
    Some("SE"),
    "JM Wizzler",
    Some("email@example.com"),
    Map("spotify" -> "https://open.spotify.com/user/wizzler"),
    Followers(None, 3829),
    "https://api.spotify.com/v1/users/wizzler",
    "wizzler",
    Seq(Image(None, "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/t1.0-1/1970403_10152215092574354_1798272330_n.jpg", None)),
    Some("premium"),
    "user",
    "spotify:user:wizzler")
  )

  /* user object (public) */
  inOut(SpotifyExamples.UserPublic, UserPublic(
    "Lilla Namo",
    Map("spotify" -> "https://open.spotify.com/user/tuggareutangranser"),
    Followers(None, 4561),
    "https://api.spotify.com/v1/users/tuggareutangranser",
    "tuggareutangranser",
    Seq(Image(None, "http://profile-images.scdn.co/artists/default/d4f208d4d49c6f3e1363765597d10c4277f5b74f", None)),
    "user",
    "spotify:user:tuggareutangranser")
  )

  /* user object (public, other) */
  inOut(SpotifyExamples.UserPublicOther, UserPublicOther(
    Map("spotify" -> "https://open.spotify.com/user/tuggareutangranser"),
    "https://api.spotify.com/v1/users/tuggareutangranser",
    "tuggareutangranser",
    "user",
    "spotify:user:tuggareutangranser")
  )

  /* Test cases */
  {
    it should s"Write/Read test case 1" in {
      val parsed = Json.parse(SpotifyExamples.TestCase1)
      parsed.validate[SavedTrack].isSuccess should be (true)
    }
  }
}
