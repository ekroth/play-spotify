package com.github.ekroth.spotify

object SpotifyExamples {
  val Image = """
{
 "height" : null,
 "url" : "http://profile-images.scdn.co/artists/default/d4f208d4d49c6f3e1363765597d10c4277f5b74f",
 "width" : null
 }
"""
  val Followers = """
{
 "href" : null,
 "total" : 4561
}
"""

  val Category = """
{
  "href" : "https://api.spotify.com/v1/browse/categories/party",
  "icons" : [ {
    "height" : 274,
    "url" : "https://datsnxq1rwndn.cloudfront.net/media/derived/party-274x274_73d1907a7371c3bb96a288390a96ee27_0_0_274_274.jpg",
    "width" : 274
  } ],
  "id" : "party",
  "name" : "Party"
}
"""

  val Copyright = """
{
      "text" : "(C) 2013 Universal Island Records, a division of Universal Music Operations Limited",
      "type" : "C"
}
"""

  val Error = """
{
  "status": 202,
  "message": "this is bad"
}
"""

  val ExternalID = """
{
    "isrc": "USR",
    "kalo": "POP"
}
"""

  val ExternalURL = """
{
  "spotify": "https://url",
  "spowtify": "https://orl"
}
"""

  val AlbumSimplified = """
{
      "album_type": "album",
      "available_markets": [ "AD", "AR" ],
      "external_urls": {
        "spotify": "https://open.spotify.com/album/34EYk8vvJHCUlNrpGxepea"
      },
      "href": "https://api.spotify.com/v1/albums/34EYk8vvJHCUlNrpGxepea",
      "id": "34EYk8vvJHCUlNrpGxepea",
      "images": [ {
        "height": 640,
        "url": "https://i.scdn.co/image/6324fe377dcedf110025527873dafc9b7ee0bb34",
        "width": 640
      }, {
        "height": 300,
        "url": "https://i.scdn.co/image/d2e2148023e8a87b7a2f8d2abdfa936154e470b8",
        "width": 300
      } ],
      "name": "Elvis 75 - Good Rockin' Tonight",
      "type": "album",
      "uri": "spotify:album:34EYk8vvJHCUlNrpGxepea"
    }
"""

  val ArtistSimplified = """
{
      "external_urls": {
        "spotify": "https://open.spotify.com/artist/43ZHCT0cAZBISjO8DG9PnE"
      },
      "href": "https://api.spotify.com/v1/artists/43ZHCT0cAZBISjO8DG9PnE",
      "id": "43ZHCT0cAZBISjO8DG9PnE",
      "name": "Elvis Presley",
      "type": "artist",
      "uri": "spotify:artist:43ZHCT0cAZBISjO8DG9PnE"
}
"""

  val PagingArrayTrackSimplified = """
{
    "href" : "https://api.spotify.com/v1/albums/0sNOF9WDwhWunNAHPD3Baj/tracks?offset=0&limit=50",
    "items" : [ {
      "artists" : [ ],
      "available_markets" : [ "AD" ],
      "disc_number" : 1,
      "duration_ms" : 305560,
      "explicit" : false,
      "external_urls" : {
        "spotify" : "https://open.spotify.com/track/3f9zqUnrnIq0LANhmnaF0V"
      },
      "href" : "https://api.spotify.com/v1/tracks/3f9zqUnrnIq0LANhmnaF0V",
      "id" : "3f9zqUnrnIq0LANhmnaF0V",
      "name" : "Money Changes Everything",
      "preview_url" : "https://p.scdn.co/mp3-preview/01bb2a6c9a89c05a4300aea427241b1719a26b06",
      "track_number" : 1,
      "type" : "track",
      "uri" : "spotify:track:3f9zqUnrnIq0LANhmnaF0V"
    } ],
    "limit" : 50,
    "next" : null,
    "offset" : 0,
    "previous" : null,
    "total" : 13
}
"""

  val AlbumFull = s"""
{
  "album_type" : "album",
  "artists" : [ {
    "external_urls" : {
      "spotify" : "https://open.spotify.com/artist/2BTZIqw0ntH9MvilQ3ewNY"
    },
    "href" : "https://api.spotify.com/v1/artists/2BTZIqw0ntH9MvilQ3ewNY",
    "id" : "2BTZIqw0ntH9MvilQ3ewNY",
    "name" : "Cyndi Lauper",
    "type" : "artist",
    "uri" : "spotify:artist:2BTZIqw0ntH9MvilQ3ewNY"
  } ],
  "available_markets" : [ "AD" ],
  "copyrights" : [ {
    "text" : "(P) 2000 Sony Music Entertainment Inc.",
    "type" : "P"
  } ],
  "external_ids" : {
    "upc" : "5099749994324"
  },
  "external_urls" : {
    "spotify" : "https://open.spotify.com/album/0sNOF9WDwhWunNAHPD3Baj"
  },
  "genres" : [ ],
  "href" : "https://api.spotify.com/v1/albums/0sNOF9WDwhWunNAHPD3Baj",
  "id" : "0sNOF9WDwhWunNAHPD3Baj",
  "images" : [ {
    "height" : 640,
    "url" : "https://i.scdn.co/image/07c323340e03e25a8e5dd5b9a8ec72b69c50089d",
    "width" : 640
  }, {
    "height" : 300,
    "url" : "https://i.scdn.co/image/8b662d81966a0ec40dc10563807696a8479cd48b",
    "width" : 300
  }, {
    "height" : 64,
    "url" : "https://i.scdn.co/image/54b3222c8aaa77890d1ac37b3aaaa1fc9ba630ae",
    "width" : 64
  } ],
  "name" : "She's So Unusual",
  "popularity" : 39,
  "release_date" : "1983",
  "release_date_precision" : "year",
  "tracks" : $PagingArrayTrackSimplified,
  "type" : "album",
  "uri" : "spotify:album:0sNOF9WDwhWunNAHPD3Baj"
}
"""

  val ArtistFull = """
{
  "external_urls" : {
    "spotify" : "https://open.spotify.com/artist/0OdUWJ0sBjDrqHygGUXeCF"
  },
  "followers" : {
    "href" : null,
    "total" : 306565
  },
  "genres" : [ "indie folk", "indie pop" ],
  "href" : "https://api.spotify.com/v1/artists/0OdUWJ0sBjDrqHygGUXeCF",
  "id" : "0OdUWJ0sBjDrqHygGUXeCF",
  "images" : [ ],
  "name" : "Band of Horses",
  "popularity" : 59,
  "type" : "artist",
  "uri" : "spotify:artist:0OdUWJ0sBjDrqHygGUXeCF"
}
"""



  val PlaylistFull = """
{
  "collaborative" : false,
  "description" : "Having friends over for dinner? Here´s the perfect playlist.",
  "external_urls" : {
    "spotify" : "http://open.spotify.com/user/spotify/playlist/59ZbFPES4DQwEjBpWHzrtC"
  },
  "followers" : {
    "href" : null,
    "total" : 143350
  },
  "href" : "https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC",
  "id" : "59ZbFPES4DQwEjBpWHzrtC",
  "images" : [ ],
  "name" : "Dinner with Friends",
  "owner" : {
    "external_urls" : {
      "spotify" : "http://open.spotify.com/user/spotify"
    },
    "href" : "https://api.spotify.com/v1/users/spotify",
    "id" : "spotify",
    "type" : "user",
    "uri" : "spotify:user:spotify"
  },
  "public" : null,
  "snapshot_id" : "bNLWdmhh+HDsbHzhckXeDC0uyKyg4FjPI/KEsKjAE526usnz2LxwgyBoMShVL+z+",
  "tracks" : {
    "href" : "https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC/tracks",
    "items" : [ ],
    "limit" : 100,
    "next" : "https://api.spotify.com/v1/users/spotify/playlists/59ZbFPES4DQwEjBpWHzrtC/tracks?offset=100&limit=100",
    "offset" : 0,
    "previous" : null,
    "total" : 105
  },
  "type" : "playlist",
  "uri" : "spotify:user:spotify:playlist:59ZbFPES4DQwEjBpWHzrtC"
}
"""

  val TrackLink = s"""
{
"external_urls": $ExternalURL,
"href": "localhost",
"id": "123",
"type": "track",
"uri": "spotify://spot"
}
"""

  val UserPrivate = """
{
  "birthdate": "1937-06-01",
  "country": "SE",
  "display_name": "JM Wizzler",
  "email": "email@example.com",
  "external_urls": {
    "spotify": "https://open.spotify.com/user/wizzler"
  },
  "followers" : {
    "href" : null,
    "total" : 3829
  },
  "href": "https://api.spotify.com/v1/users/wizzler",
  "id": "wizzler",
  "images": [
    {
      "height": null,
      "url": "https://fbcdn-profile-a.akamaihd.net/hprofile-ak-frc3/t1.0-1/1970403_10152215092574354_1798272330_n.jpg",
      "width": null
    }
  ],
  "product": "premium",
  "type": "user",
  "uri": "spotify:user:wizzler"
}
"""

  val UserPublic = """
{
 "display_name" : "Lilla Namo",
 "external_urls" : {
 "spotify" : "https://open.spotify.com/user/tuggareutangranser"
 },
 "followers" : {
 "href" : null,
 "total" : 4561
 },
 "href" : "https://api.spotify.com/v1/users/tuggareutangranser",
 "id" : "tuggareutangranser",
 "images" : [ {
 "height" : null,
 "url" : "http://profile-images.scdn.co/artists/default/d4f208d4d49c6f3e1363765597d10c4277f5b74f",
 "width" : null
 } ],
 "type" : "user",
 "uri" : "spotify:user:tuggareutangranser"
}
"""

  val UserPublicOther = """
{
 "external_urls" : {
 "spotify" : "https://open.spotify.com/user/tuggareutangranser"
 },
 "href" : "https://api.spotify.com/v1/users/tuggareutangranser",
 "id" : "tuggareutangranser",
 "type" : "user",
 "uri" : "spotify:user:tuggareutangranser"
}
"""

  val TrackSimplified = """
{
    "artists": [ {
      "external_urls": {
        "spotify": "https://open.spotify.com/artist/08td7MxkoHQkXnWAYD8d6Q"
      },
      "href": "https://api.spotify.com/v1/artists/08td7MxkoHQkXnWAYD8d6Q",
      "id": "08td7MxkoHQkXnWAYD8d6Q",
      "name": "Tania Bowra",
      "type": "artist",
      "uri": "spotify:artist:08td7MxkoHQkXnWAYD8d6Q"
    } ],
    "available_markets": [ "AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY" ],
    "disc_number": 1,
    "duration_ms": 276773,
    "explicit": false,
    "external_urls": {
      "spotify": "https://open.spotify.com/track/2TpxZ7JUBn3uw46aR7qd6V"
    },
    "href": "https://api.spotify.com/v1/tracks/2TpxZ7JUBn3uw46aR7qd6V",
    "id": "2TpxZ7JUBn3uw46aR7qd6V",
    "name": "All I Want",
    "preview_url": "https://p.scdn.co/mp3-preview/6d00206e32194d15df329d4770e4fa1f2ced3f57",
    "track_number": 1,
    "type": "track",
    "uri": "spotify:track:2TpxZ7JUBn3uw46aR7qd6V"
}
"""

  val PagingEmpty = """
{
    "href" : "https://api.spotify.com/v1/browse/categories/party/playlists?country=BR&offset=0&limit=20",
    "items" : [ ],
    "limit" : 20,
    "next" : "https://api.spotify.com/v1/browse/categories/party/playlists?country=BR&offset=20&limit=20",
    "offset" : 0,
    "previous" : null,
    "total" : 148
}
"""

  val PlaylistSimplified = s"""
{
      "collaborative" : false,
      "external_urls" : {
        "spotify" : "http://open.spotify.com/user/spotifybrazilian/playlist/4k7EZPI3uKMz4aRRrLVfen"
      },
      "href" : "https://api.spotify.com/v1/users/spotifybrazilian/playlists/4k7EZPI3uKMz4aRRrLVfen",
      "id" : "4k7EZPI3uKMz4aRRrLVfen",
      "images" : [ {
        "height" : 300,
        "url" : "https://i.scdn.co/image/bf6544c213532e9650088dfef76c8521093d970e",
        "width" : 300
      } ],
      "name" : "Noite Eletrônica",
      "owner" : $UserPublic,
      "public" : null,
      "tracks" : {
        "href" : "https://api.spotify.com/v1/users/spotifybrazilian/playlists/4k7EZPI3uKMz4aRRrLVfen/tracks",
        "total" : 80
      },
      "type" : "playlist",
      "uri" : "spotify:user:spotifybrazilian:playlist:4k7EZPI3uKMz4aRRrLVfen"
}
"""

  val Tracks = """
{
        "href" : "https://api.spotify.com/v1/users/spotifybrazilian/playlists/4k7EZPI3uKMz4aRRrLVfen/tracks",
        "total" : 80
}
"""

  val TrackFull = """
{
    "album": {
      "album_type": "album",
      "available_markets": [ "AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY" ],
      "external_urls": {
        "spotify": "https://open.spotify.com/album/34EYk8vvJHCUlNrpGxepea"
      },
      "href": "https://api.spotify.com/v1/albums/34EYk8vvJHCUlNrpGxepea",
      "id": "34EYk8vvJHCUlNrpGxepea",
      "images": [ ],
      "name": "Elvis 75 - Good Rockin' Tonight",
      "type": "album",
      "uri": "spotify:album:34EYk8vvJHCUlNrpGxepea"
    },
    "artists": [ {
      "external_urls": {
        "spotify": "https://open.spotify.com/artist/43ZHCT0cAZBISjO8DG9PnE"
      },
      "href": "https://api.spotify.com/v1/artists/43ZHCT0cAZBISjO8DG9PnE",
      "id": "43ZHCT0cAZBISjO8DG9PnE",
      "name": "Elvis Presley",
      "type": "artist",
      "uri": "spotify:artist:43ZHCT0cAZBISjO8DG9PnE"
    } ],
    "available_markets": [ "AD", "AR", "AT", "AU", "BE", "BG", "BO", "BR", "CA", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MX", "MY", "NI", "NL", "NO", "NZ", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "US", "UY" ],
    "disc_number": 3,
    "duration_ms": 260973,
    "explicit": false,
    "external_ids": {
      "isrc": "USRC16901355"
    },
    "external_urls": {
      "spotify": "https://open.spotify.com/track/6fgjU6IfBOXHI3OKtndEeE"
    },
    "href": "https://api.spotify.com/v1/tracks/6fgjU6IfBOXHI3OKtndEeE",
    "id": "6fgjU6IfBOXHI3OKtndEeE",
    "name": "Suspicious Minds",
    "popularity": 70,
    "preview_url": "https://p.scdn.co/mp3-preview/3742af306537513a4f446d7c8f9cdb1cea6e36d1",
    "track_number": 19,
    "type": "track",
    "uri": "spotify:track:6fgjU6IfBOXHI3OKtndEeE"
}
"""

  val SavedTrack = s"""
{
      "added_at": "2014-07-08T14:05:27Z",
      "track": $TrackFull
}
"""

  val PlaylistTrack = s"""
{
      "added_at" : "2014-09-01T04:21:28Z",
      "added_by" : {
        "external_urls" : { },
        "href" : "https://api.spotify.com/v1/users/spotify",
        "id" : "spotify",
        "type" : "user",
        "uri" : "spotify:user:spotify"
      },
      "track" : $TrackFull
}
"""

  val PagingSavedTrack = """

  "href": "https://api.spotify.com/v1/me/tracks?offset=0&limit=20",
  "items": [
    {
      "added_at": "2014-07-08T14:05:27Z",
      "track": {
        "album": {
          "album_type": "album",
          "available_markets": [
            "AD",
            "AR",
            "AT",
            "TR",
            "TW",
            "UY"
          ],
          "external_urls": {
            "spotify": "https://open.spotify.com/album/4kbE34G5bxaxwuCqz0NEw4"
          },
          "href": "https://api.spotify.com/v1/albums/4kbE34G5bxaxwuCqz0NEw4",
          "id": "4kbE34G5bxaxwuCqz0NEw4",
          "images": [
            {
              "height": 635,
              "url": "https://i.scdn.co/image/5ac900806189613a98ce8d2a979265dabd3f7347",
              "width": 640
            },
            {
              "height": 298,
              "url": "https://i.scdn.co/image/e531cef3541f3d9d7fef9dbede8f19223e2f1497",
              "width": 300
            },
            {
              "height": 64,
              "url": "https://i.scdn.co/image/4be3ce9447365df0b8653f941058ab3fd7177b25",
              "width": 64
            }
          ],
          "name": "The Best Of Me",
          "type": "album",
          "uri": "spotify:album:4kbE34G5bxaxwuCqz0NEw4"
        },
        "artists": [
          {
            "external_urls": {
              "spotify": "https://open.spotify.com/artist/3Z02hBLubJxuFJfhacLSDc"
            },
            "href": "https://api.spotify.com/v1/artists/3Z02hBLubJxuFJfhacLSDc",
            "id": "3Z02hBLubJxuFJfhacLSDc",
            "name": "Bryan Adams",
            "type": "artist",
            "uri": "spotify:artist:3Z02hBLubJxuFJfhacLSDc"
          }
        ],
        "available_markets": [
          "AD",
          "AR",
          "AT",
          "TR",
          "TW",
          "UY"
        ],
        "disc_number": 1,
        "duration_ms": 265933,
        "explicit": false,
        "external_ids": {
          "isrc": "USAM19774904"
        },
        "external_urls": {
          "spotify": "https://open.spotify.com/track/1XjKmqLHqnzNLYqYSRBIZK"
        },
        "href": "https://api.spotify.com/v1/tracks/1XjKmqLHqnzNLYqYSRBIZK",
        "id": "1XjKmqLHqnzNLYqYSRBIZK",
        "name": "Back To You - MTV Unplugged Version",
        "popularity": 43,
        "preview_url": "https://p.scdn.co/mp3-preview/abeb349e0ea95846b4e4e01b115fcdbd5e9a991a",
        "track_number": 11,
        "type": "track",
        "uri": "spotify:track:1XjKmqLHqnzNLYqYSRBIZK"
      }
    }
  ],
  "limit": 20,
  "next": "https://api.spotify.com/v1/me/tracks?offset=20&limit=20",
  "offset": 0,
  "previous": null,
  "total": 53
"""

  val TestCase1 = """
{
"added_at" : "2014-10-26T13:26:56Z",
    "track" : {
      "album" : {
        "album_type" : "album",
        "available_markets" : [ "AD", "AR", "AT", "BE", "BG", "BO", "BR", "CH", "CL", "CO", "CR", "CY", "CZ", "DE", "DK", "DO", "EC", "EE", "ES", "FI", "FR", "GB", "GR", "GT", "HK", "HN", "HU", "IE", "IS", "IT", "LI", "LT", "LU", "LV", "MC", "MT", "MY", "NI", "NL", "NO", "PA", "PE", "PH", "PL", "PT", "PY", "RO", "SE", "SG", "SI", "SK", "SV", "TR", "TW", "UY" ],
        "external_urls" : {
          "spotify" : "https://open.spotify.com/album/0JGCvHdtAPddamH80fV4Z6"
        },
        "href" : "https://api.spotify.com/v1/albums/0JGCvHdtAPddamH80fV4Z6",
        "id" : "0JGCvHdtAPddamH80fV4Z6",
        "images" : [ {
          "height" : 640,
          "url" : "https://i.scdn.co/image/3c6fd318d65fa1db702bf7fb2509c52e041596e1",
          "width" : 640
        }, {
          "height" : 300,
          "url" : "https://i.scdn.co/image/8081e470a8eb2ca14dd61102a06f8696aad2c2d2",
          "width" : 300
        }, {
          "height" : 64,
          "url" : "https://i.scdn.co/image/57a4a77fa7539212e03d17a9bc997ea8701c7c41",
          "width" : 64
        } ],
        "name" : "In Rolling Waves",
        "type" : "album",
        "uri" : "spotify:album:0JGCvHdtAPddamH80fV4Z6"
      },
      "artists" : [ {
        "external_urls" : {
          "spotify" : "https://open.spotify.com/artist/3Qy1IxDSU8SLpUUOfbOpxM"
        },
        "href" : "https://api.spotify.com/v1/artists/3Qy1IxDSU8SLpUUOfbOpxM",
        "id" : "3Qy1IxDSU8SLpUUOfbOpxM",
        "name" : "The Chain Gang Of 1974",
        "type" : "artist",
        "uri" : "spotify:artist:3Qy1IxDSU8SLpUUOfbOpxM"
      } ],
      "available_markets" : [ ],
      "disc_number" : 2,
      "duration_ms" : 254400,
      "explicit" : false,
      "external_ids" : {
        "isrc" : "USWB11401589"
      },
      "external_urls" : {
        "spotify" : "https://open.spotify.com/track/4z7Ufu7CrBFyTHT3ymBdVi"
      },
      "href" : "https://api.spotify.com/v1/tracks/4z7Ufu7CrBFyTHT3ymBdVi",
      "id" : "4z7Ufu7CrBFyTHT3ymBdVi",
      "name" : "What We Want",
      "popularity" : 0,
      "preview_url" : null,
      "track_number" : 9,
      "type" : "track",
      "uri" : "spotify:track:4z7Ufu7CrBFyTHT3ymBdVi"
    }
  }
"""

}
