/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

// scalastyle:off number.of.types
// scalastyle:off number.of.methods

package com.github.ekroth
package spotify

/** Objects corresponding to Spotify's object model.
  *
  * Since this trait contains classes, it should only be
  * extended by the package object.
  */
trait Objects {

  import scala.collection.immutable.Seq

  import spray.json._

  type ExternalID = Map[String, String]
  type ExternalURL = Map[String, String]
  type Timestamp = String
  type SpotifyID = String
  type SpotifyURI = String
  type AuthCode = String
  type AccessToken = String
  type Scope = String

  /** Rename json names that are reserved words in Scala.
    *
    * In order to be able to use the Json.writes/reads macro we
    * need to pre- and post-process the JsValues accordingly.
    */
  implicit class RichJsonFormat[T](val underlying: JsonFormat[T]) {

    private def fix(in: String, out: String, obj: JsValue): JsValue = obj match {
      case JsObject(fields) => JsObject(fields.map {
        case (`in`, v) => (out, v)
        case x => x
      })

      case x => x
    }

    def withTipe: JsonFormat[T] = new JsonFormat[T] {
      override def write(obj: T): JsValue = fix("tipe", "type", underlying.write(obj))
      override def read(obj: JsValue): T = underlying.read(fix("type", "tipe", obj))
    }
  }

  object Image extends DefaultJsonProtocol {
    implicit val imageFormat = jsonFormat3(Image.apply).withTipe
  }
  case class Image(height: Option[Int], url: String, width: Option[Int])

  object Tracks extends DefaultJsonProtocol {
    implicit val tracksFormat = jsonFormat2(Tracks.apply).withTipe
  }
  case class Tracks(href: String, total: Int)

  object Followers extends DefaultJsonProtocol {
    implicit val followersFormat = jsonFormat2(Followers.apply).withTipe
  }
  case class Followers(href: Option[String], total: Int)

  /** Note: It turns out that sometimes Spotify skip name/followers/images for UserPublic.
    * https://developer.spotify.com/web-api/object-model/#comment-1769235635
    */
  object UserPublicOther extends DefaultJsonProtocol {
    implicit val userPublicOtherFormat = jsonFormat5(UserPublicOther.apply).withTipe
  }
  case class UserPublicOther(external_urls: ExternalURL, href: String, id: String, tipe: String, uri: String)

  object UserPublic extends DefaultJsonProtocol {
    implicit val userPublicFormat = jsonFormat8(UserPublic.apply).withTipe
  }
  case class UserPublic(display_name: String, external_urls: ExternalURL, followers: Followers,
    href: String, id: String, images: Seq[Image], tipe: String, uri: String)

  object AlbumSimplified extends DefaultJsonProtocol {
    implicit val albumSimplifiedFormat = jsonFormat9(AlbumSimplified.apply).withTipe
  }
  case class AlbumSimplified(album_type: String, available_markets: Seq[String], external_urls: ExternalURL,
    href: String, id: String, images: Seq[Image], name: String, tipe: String, uri: String)

  object ArtistSimplified extends DefaultJsonProtocol {
    implicit val artistSimplifiedFormat = jsonFormat6(ArtistSimplified.apply).withTipe
  }
  case class ArtistSimplified(external_urls: ExternalURL, href: String, id: String, name: String, tipe: String, uri: String)

  object TrackLink extends DefaultJsonProtocol {
    implicit val trackLinkFormat = jsonFormat5(TrackLink.apply).withTipe
  }
  case class TrackLink(external_urls: ExternalURL, href: String, id: String, tipe: String, uri: String)

  object TrackFull extends DefaultJsonProtocol {
    implicit val trackFullFormat = jsonFormat18(TrackFull.apply).withTipe
  }
  case class TrackFull(album: AlbumSimplified, artists: Seq[ArtistSimplified], available_markets: Seq[String],
    disc_number: Int, duration_ms: Int, explicit: Boolean, external_ids: ExternalID, external_urls: ExternalURL,
    href: String, id: String, is_playable: Option[Boolean], linked_from: Option[TrackLink], name: String, popularity: Int,
    preview_url: Option[String], track_number: Int, tipe: String, uri: String)

  object PlaylistTrack extends DefaultJsonProtocol {
    implicit val playlistTrackFormat = jsonFormat3(PlaylistTrack.apply).withTipe
  }
  case class PlaylistTrack(added_at: Option[Timestamp], added_by: UserPublicOther, track: TrackFull)

  object TrackSimplified extends DefaultJsonProtocol {
    implicit val trackSimplifiedFormat = jsonFormat15(TrackSimplified.apply).withTipe
  }
  case class TrackSimplified(artists: Seq[ArtistSimplified], available_markets: Seq[String], disc_number: Int,
    duration_ms: Int, explicit: Boolean, external_urls: ExternalURL, href: String, id: String, is_playable: Option[Boolean],
    linked_from: Option[TrackLink], name: String, preview_url: String, track_number: Int, tipe: String, uri: String)

  object Paging extends DefaultJsonProtocol {
    implicit def pagingFormat[T : JsonFormat] = jsonFormat6(Paging.apply[T]).withTipe
  }
  case class Paging[T](href: String, items: Seq[T], limit: Int, next: Option[String], previous: Option[String], total: Int)

  object Copyright extends DefaultJsonProtocol {
    implicit val copyrightFormat = jsonFormat2(Copyright.apply).withTipe
  }
  case class Copyright(text: String, tipe: String)

  object AlbumFull extends DefaultJsonProtocol {
    implicit val albumFullFormat = jsonFormat17(AlbumFull.apply).withTipe
  }
  case class AlbumFull(album_type: String, artists: Seq[ArtistSimplified], available_markets: Seq[String],
    copyrights: Seq[Copyright], external_ids: ExternalID, external_urls: ExternalURL, genres: Seq[String],
    href: String, id: String, images: Seq[Image], name: String, popularity: Int, release_date: String,
    release_date_precision: String, tracks: Paging[TrackSimplified], tipe: String, uri: String)

  object ArtistFull extends DefaultJsonProtocol {
    implicit val artistFullFormat = jsonFormat10(ArtistFull.apply).withTipe
  }
  case class ArtistFull(external_urls: ExternalURL, followers: Followers, genres: Seq[String], href: String,
    id: String, images: Seq[Image], name: String, popularity: Int, tipe: String, uri: String)

  object Category extends DefaultJsonProtocol {
    implicit val categoryFormat = jsonFormat4(Category.apply).withTipe
  }
  case class Category(href: String, icons: Seq[Image], id: String, name: String)

  object ErrorMessage extends DefaultJsonProtocol {
    implicit val errorMessageFormat = jsonFormat2(ErrorMessage.apply).withTipe
  }
  case class ErrorMessage(status: Int, message: String)

  object PlaylistFull extends DefaultJsonProtocol {
    implicit val playlistFullFormat = jsonFormat14(PlaylistFull.apply).withTipe
  }
  case class PlaylistFull(collaborative: Boolean, description: Option[String], external_urls: ExternalURL,
    followers: Followers, href: String, id: String, images: Seq[Image], name: String, owner: UserPublicOther,
    public: Option[Boolean], snapshot_id: String, tracks: Paging[PlaylistTrack], tipe: String, uri: String)

  object PlaylistSimplified extends DefaultJsonProtocol {
    implicit val playlistSimplifiedFormat = jsonFormat11(PlaylistSimplified.apply).withTipe
  }
  case class PlaylistSimplified(collaborative: Boolean, external_urls: ExternalURL, href: String, id: String,
    images: Seq[Image], name: String, owner: UserPublic, public: Option[Boolean], tracks: Tracks, tipe: String, uri: String)

  object SavedTrack extends DefaultJsonProtocol {
    implicit val savedTrackFormat = jsonFormat2(SavedTrack.apply).withTipe
  }
  case class SavedTrack(added_at: Timestamp, track: TrackFull)

  object UserPrivate extends DefaultJsonProtocol {
    implicit val userPrivateFormat = jsonFormat12(UserPrivate.apply).withTipe
  }
  case class UserPrivate(birthdate: Option[String], country: Option[String], display_name: String, email: Option[String],
    external_urls: ExternalURL, followers: Followers, href: String, id: String, images: Seq[Image], product: Option[String],
    tipe: String, uri: String)
}
