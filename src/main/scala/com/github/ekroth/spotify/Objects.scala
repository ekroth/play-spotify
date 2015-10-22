/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

// scalastyle:off number.of.types
// scalastyle:off number.of.methods

package com.github.ekroth.spotify

/** Objects corresponding to Spotify's object model.
  *
  * Since this trait contains classes, it should only be
  * extended by the package object.
  */
private[spotify] trait Objects {

  import scala.collection.immutable.Seq
  import play.api.libs.json._
  import play.api.libs.functional.syntax._

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
  object TypeNameFix {
    private[this] def replace(in: String, out: String)(json: JsValue): JsValue = json match {
      case x: JsObject => (x \ in) match {
        case y: JsString => (x - in) + (out, y)
        case _ => x
      }
      case x => x
    }

    /** Convert from 'name' to 'keyword'. */
    val afterWrite: JsValue => JsValue = replace("tipe", "type")(_)

    /** Convert from 'keyword' to 'name'. */
    val beforeRead = Reads[JsValue] { js => JsSuccess(replace("type", "tipe")(js)) }
  }

  object Image {
    implicit val ImageWrites = Json.writes[Image].transform(TypeNameFix.afterWrite)
    implicit val ImageReads = Json.reads[Image].compose(TypeNameFix.beforeRead)
  }
  case class Image(height: Option[Int], url: String, width: Option[Int])

  object Tracks {
    implicit val TracksWrites = Json.writes[Tracks].transform(TypeNameFix.afterWrite)
    implicit val TracksReads = Json.reads[Tracks].compose(TypeNameFix.beforeRead)
  }
  case class Tracks(href: String, total: Int)

  object Followers {
    implicit val FollowersWrites = Json.writes[Followers].transform(TypeNameFix.afterWrite)
    implicit val FollowersReads = Json.reads[Followers].compose(TypeNameFix.beforeRead)
  }
  case class Followers(href: Option[String], total: Int)

  /** Note: It turns out that sometimes Spotify skip name/followers/images for UserPublic.
    * https://developer.spotify.com/web-api/object-model/#comment-1769235635
    */
  object UserPublicOther {
    implicit val UserPublicOtherWrites = Json.writes[UserPublicOther].transform(TypeNameFix.afterWrite)
    implicit val UserPublicOtherReads = Json.reads[UserPublicOther].compose(TypeNameFix.beforeRead)
  }
  case class UserPublicOther(external_urls: ExternalURL, href: String, id: String, tipe: String, uri: String)

  object UserPublic {
    implicit val UserPublicWrites = Json.writes[UserPublic].transform(TypeNameFix.afterWrite)
    implicit val UserPublicReads = Json.reads[UserPublic].compose(TypeNameFix.beforeRead)
  }
  case class UserPublic(display_name: String, external_urls: ExternalURL, followers: Followers,
    href: String, id: String, images: Seq[Image], tipe: String, uri: String)

  object AlbumSimplified {
    implicit val AlbumSimplifiedWrites = Json.writes[AlbumSimplified].transform(TypeNameFix.afterWrite)
    implicit val AlbumSimplifiedReads = Json.reads[AlbumSimplified].compose(TypeNameFix.beforeRead)
  }
  case class AlbumSimplified(album_type: String, available_markets: Seq[String], external_urls: ExternalURL,
    href: String, id: String, images: Seq[Image], name: String, tipe: String, uri: String)

  object ArtistSimplified {
    implicit val ArtistSimplifiedWrites = Json.writes[ArtistSimplified].transform(TypeNameFix.afterWrite)
    implicit val ArtistSimplifiedReads = Json.reads[ArtistSimplified].compose(TypeNameFix.beforeRead)
  }
  case class ArtistSimplified(external_urls: ExternalURL, href: String, id: String, name: String, tipe: String, uri: String)

  object TrackLink {
    implicit val TrackLinkWrites = Json.writes[TrackLink].transform(TypeNameFix.afterWrite)
    implicit val TrackLinkReads = Json.reads[TrackLink].compose(TypeNameFix.beforeRead)
  }
  case class TrackLink(external_urls: ExternalURL, href: String, id: String, tipe: String, uri: String)

  object TrackFull {
    implicit val TrackFullWrites = Json.writes[TrackFull].transform(TypeNameFix.afterWrite)
    implicit val TrackFullReads = Json.reads[TrackFull].compose(TypeNameFix.beforeRead)
  }
  case class TrackFull(album: AlbumSimplified, artists: Seq[ArtistSimplified], available_markets: Seq[String],
    disc_number: Int, duration_ms: Int, explicit: Boolean, external_ids: ExternalID, external_urls: ExternalURL,
    href: String, id: String, is_playable: Option[Boolean], linked_from: Option[TrackLink], name: String, popularity: Int,
    preview_url: Option[String], track_number: Int, tipe: String, uri: String)

  object PlaylistTrack {
    implicit val PlaylistTrackWrites = Json.writes[PlaylistTrack].transform(TypeNameFix.afterWrite)
    implicit val PlaylistTrackReads = Json.reads[PlaylistTrack].compose(TypeNameFix.beforeRead)
  }
  case class PlaylistTrack(added_at: Option[Timestamp], added_by: UserPublicOther, track: TrackFull)

  object TrackSimplified {
    implicit val TrackSimplifiedWrites = Json.writes[TrackSimplified].transform(TypeNameFix.afterWrite)
    implicit val TrackSimplifiedReads = Json.reads[TrackSimplified].compose(TypeNameFix.beforeRead)
  }
  case class TrackSimplified(artists: Seq[ArtistSimplified], available_markets: Seq[String], disc_number: Int,
    duration_ms: Int, explicit: Boolean, external_urls: ExternalURL, href: String, id: String, is_playable: Option[Boolean],
    linked_from: Option[TrackLink], name: String, preview_url: String, track_number: Int, tipe: String, uri: String)

  /** Paging represents both the Paging and the Cursor based paging object.
    * The Json reads/writes macro can't handle generics very well.
    */
  object Paging {
    // scalastyle:off method.name

    implicit def PagingWrites[T : Writes]: Writes[Paging[T]] = (
      (JsPath \ "href").write[String] and
        (JsPath \ "items").write[Seq[T]] and
        (JsPath \ "limit").write[Int] and
        (JsPath \ "next").writeNullable[String] and
        (JsPath \ "previous").writeNullable[String] and
        (JsPath \ "total").write[Int]
    )(unlift(Paging.unapply[T])).transform(TypeNameFix.afterWrite)

    implicit def PagingReads[T : Reads]: Reads[Paging[T]] = (
      (JsPath \ "href").read[String] and
        (JsPath \ "items").read[Seq[T]] and
        (JsPath \ "limit").read[Int] and
        (JsPath \ "next").readNullable[String] and
        (JsPath \ "previous").readNullable[String] and
        (JsPath \ "total").read[Int]
    )(Paging.apply[T] _).compose(TypeNameFix.beforeRead)
  }
  case class Paging[T](href: String, items: Seq[T], limit: Int, next: Option[String], previous: Option[String], total: Int)

  object Copyright {
    implicit val CopyrightWrites = Json.writes[Copyright].transform(TypeNameFix.afterWrite)
    implicit val CopyrightReads = Json.reads[Copyright].compose(TypeNameFix.beforeRead)
  }
  case class Copyright(text: String, tipe: String)

  object AlbumFull {
    implicit val AlbumFullWrites = Json.writes[AlbumFull].transform(TypeNameFix.afterWrite)
    implicit val AlbumFullReads = Json.reads[AlbumFull].compose(TypeNameFix.beforeRead)
  }
  case class AlbumFull(album_type: String, artists: Seq[ArtistSimplified], available_markets: Seq[String],
    copyrights: Seq[Copyright], external_ids: ExternalID, external_urls: ExternalURL, genres: Seq[String],
    href: String, id: String, images: Seq[Image], name: String, popularity: Int, release_date: String,
    release_date_precision: String, tracks: Paging[TrackSimplified], tipe: String, uri: String)

  object ArtistFull {
    implicit val ArtistFullWrites = Json.writes[ArtistFull].transform(TypeNameFix.afterWrite)
    implicit val ArtistFullReads = Json.reads[ArtistFull].compose(TypeNameFix.beforeRead)
  }
  case class ArtistFull(external_urls: ExternalURL, followers: Followers, genres: Seq[String], href: String,
    id: String, images: Seq[Image], name: String, popularity: Int, tipe: String, uri: String)

  object Category {
    implicit val CategoryWrites = Json.writes[Category].transform(TypeNameFix.afterWrite)
    implicit val CategoryReads = Json.reads[Category].compose(TypeNameFix.beforeRead)
  }
  case class Category(href: String, icons: Seq[Image], id: String, name: String)

  object ErrorMessage {
    implicit val ErrorMessageWrites = Json.writes[ErrorMessage].transform(TypeNameFix.afterWrite)
    implicit val ErrorMessageReads = Json.reads[ErrorMessage].compose(TypeNameFix.beforeRead)
  }
  case class ErrorMessage(status: Int, message: String)

  object PlaylistFull {
    implicit val PlaylistFullWrites = Json.writes[PlaylistFull].transform(TypeNameFix.afterWrite)
    implicit val PlaylistFullReads = Json.reads[PlaylistFull].compose(TypeNameFix.beforeRead)
  }
  case class PlaylistFull(collaborative: Boolean, description: Option[String], external_urls: ExternalURL,
    followers: Followers, href: String, id: String, images: Seq[Image], name: String, owner: UserPublicOther,
    public: Option[Boolean], snapshot_id: String, tracks: Paging[PlaylistTrack], tipe: String, uri: String)

  object PlaylistSimplified {
    implicit val PlaylistSimplifiedWrites = Json.writes[PlaylistSimplified].transform(TypeNameFix.afterWrite)
    implicit val PlaylistSimplifiedReads = Json.reads[PlaylistSimplified].compose(TypeNameFix.beforeRead)
  }
  case class PlaylistSimplified(collaborative: Boolean, external_urls: ExternalURL, href: String, id: String,
    images: Seq[Image], name: String, owner: UserPublic, public: Option[Boolean], tracks: Tracks, tipe: String, uri: String)

  object SavedTrack {
    implicit val SavedTrackWrites = Json.writes[SavedTrack].transform(TypeNameFix.afterWrite)
    implicit val SavedTrackReads = Json.reads[SavedTrack].compose(TypeNameFix.beforeRead)
  }
  case class SavedTrack(added_at: Timestamp, track: TrackFull)

  object UserPrivate {
    implicit val UserPrivateWrites = Json.writes[UserPrivate].transform(TypeNameFix.afterWrite)
    implicit val UserPrivateReads = Json.reads[UserPrivate].compose(TypeNameFix.beforeRead)
  }
  case class UserPrivate(birthdate: Option[String], country: Option[String], display_name: String, email: Option[String],
    external_urls: ExternalURL, followers: Followers, href: String, id: String, images: Seq[Image], product: Option[String],
    tipe: String, uri: String)
}
