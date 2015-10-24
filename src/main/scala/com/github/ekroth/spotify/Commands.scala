/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth
package spotify

/** Commands corresponding to the Spotify Web API v1. */
trait Commands {
  self: Caching with Extensions =>

  import scala.collection.immutable.Seq
  import scala.concurrent.{ ExecutionContext, Future }

  import scalaz._
  import Scalaz._

  import akka.actor.ActorSystem
  import akka.http.scaladsl.model.headers.{ Authorization, OAuth2BearerToken, RawHeader }
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import akka.http.scaladsl.model.{ HttpRequest, Uri }
  import akka.http.scaladsl.model.Uri.Path
  import akka.http.scaladsl.model.HttpMethods.{ GET, POST }
  import akka.http.scaladsl.unmarshalling.Unmarshal
  import akka.stream.Materializer
  import spray.json._
  import DefaultJsonProtocol._

  import errorhandling._

  private[spotify] val accountsBaseUri = Uri.Empty.withScheme(Uri.httpScheme(true)).withHost("accounts.spotify.com")
  private[spotify] val baseUri = Uri.Empty.withScheme(Uri.httpScheme(true)).withHost("api.spotify.com").withPath(Path / "v1")
  private[spotify] val tokenUri = accountsBaseUri.withPath(Path / "api" / "token")
  private[spotify] val meUri = baseUri.withPath(Path / "me")

  val spotifyMaxOffset = Int.MaxValue
  val spotifyMaxLimit = 50

  private[spotify] trait ScopeTag
  private[spotify] type Scope = String @@ ScopeTag

  /** Scopes for user access. */
  object Scope {
    private[spotify] def apply(s: String): Scope = Tag.of[ScopeTag](s)
    private[spotify] def unwrap(s: Scope): String = Tag.of[ScopeTag].unwrap(s)

    val playlistModifyPrivate: Scope = Scope("playlist-modify-private")
    val playlistModifyPublic:  Scope = Scope("playlist-modify-public")
    val playlistReadPrivate:   Scope = Scope("playlist-read-private")
    val streaming:             Scope = Scope("streaming")
    val userFollowModify:      Scope = Scope("user-follow-modify")
    val userFollowRead:        Scope = Scope("user-follow-read")
    val userLibraryModify:     Scope = Scope("user-library-modify")
    val userLibraryRead:       Scope = Scope("user-library-read")
    val userReadBirthdate:     Scope = Scope("user-read-birthdate")
    val userReadEmail:         Scope = Scope("user-read-email")
    val userReadPrivate:       Scope = Scope("user-read-private")

    val all: Seq[Scope] = Seq(
      playlistModifyPrivate,
      playlistModifyPublic,
      playlistReadPrivate,
      streaming,
      userFollowModify,
      userFollowRead,
      userLibraryModify,
      userLibraryRead,
      userReadBirthdate,
      userReadEmail,
      userReadPrivate
    )
  }

  /** Create a redirect URL.
    *
    * The server generates a state variable, selects some scopes and
    * redirects the user to this URL. The user is then redirected back
    * to the redirect URL set in `spotify.Credentials`.
    *
    * @param state Optional state variable.
    * @param scopes Scopes.
    */
  def redirectUri(state: Option[String], scopes: Scope*)(implicit srv: Credentials): Uri = {
    val base = accountsBaseUri.withPath(Path / "authorize").withQuery(
      ("response_type", "code"),
      ("client_id", srv.clientId),
      ("redirect_uri", srv.redirectUri))


    val withState = state.toList.map(s => ("state", s))
    val withScopes = scopes.toList.map(s => ("scope", Scope.unwrap(s)))
    base.withQuery((withState ++ withScopes): _*)
  }

  private[spotify] def get[T : JsonFormat](uri: Uri, token: Token, inner: Option[String])
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[T] = Result.async {
    (for {
      resp <- Http().singleRequest(HttpRequest(
        GET,
        uri = uri,
        headers = Seq(Authorization(OAuth2BearerToken(token.accessToken)))))
      jsonResp <- Unmarshal(resp.entity).to[JsValue]
    } yield {
      val js = inner.map { x =>
        val JsObject(fields) = jsonResp.asJsObject
        fields(x)
      }.getOrElse(jsonResp)

      js.convertTo[T].right
    }).recover {
      case x: Exception => SpotifyError.Thrown(x).left
      case x => SpotifyError.Unknown(s"During `get`: $x").left
     }
  }

  /** Get the current user's private profile. */
  def currentUserProfile(user: UserAuth)(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[UserPrivate] =
    get[UserPrivate](meUri, user, None)

  /** Get the current user's liked tracks. */
  def currentUserTracks(user: UserAuth, limit: Int = spotifyMaxLimit)
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[Pager[SavedTrack]] = {
    requireBounds(1, limit, spotifyMaxLimit, "limit")
    get[Paging[SavedTrack]](
      meUri.withPath(Path / "tracks").withQuery(("limit", limit.toString)),
      user,
      None).map(_.withExt())
  }

  def currentUserFollowedArtists(user: UserAuth, limit: Int = spotifyMaxLimit)
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[Pager[ArtistFull]] = {
    requireBounds(1, limit, spotifyMaxLimit, "limit")
    get[Paging[ArtistFull]](
      meUri.withPath(Path / "following").withQuery(("type", "artist"), ("limit", limit.toString)),
      user,
      Some("artists")).map(_.withExt(Some("artists")))
  }

  def currentUserIsFollowing(user: UserAuth, ids: Seq[String])
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[Seq[(String, Boolean)]] = {
    requireBounds(1, ids.size, spotifyMaxLimit, "ids")
    get[Seq[Boolean]](
      meUri.withPath(Path / "following" / "contains").withQuery(("type", "artist"), ("ids", ids.mkString(","))),
      user,
      None).map(x => ids.zip(x))
  }

  def searchArtist(client: ClientAuth, query: String, limit: Int = spotifyMaxLimit)
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[Pager[ArtistFull]] = {
    requireBounds(1, limit, spotifyMaxLimit, "limit")
    get[Paging[ArtistFull]](
      baseUri.withPath(Path / "search").withQuery(("type", "artist"), ("q", query)),
      client,
      Some("artists")).map(_.withExt(Some("artists")))
  }

  /** Refresh user token.
    *
    * This requires that the user has been authorized before and is
    * available in the Cache. The cache is refreshed if the user
    * authorization refresh is successful.
    */
  def userRefresh(authCode: AuthCode)(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): ResultF[Option[UserAuth]] = Result.async {
    loadUser(authCode) match {
      case Some(user) => {
        (for {
          post <- Http().singleRequest(HttpRequest(
            POST,
            uri = tokenUri,
            headers = Seq(
              RawHeader("grant_type", "refresh_token"),
              RawHeader("refresh_token", user.refreshToken),
              RawHeader("client_id", srv.clientId),
              RawHeader("client_secret", srv.clientSecret))))
          jsonResp <- Unmarshal(post.entity).to[JsValue]
        } yield {
          val JsObject(fields) = jsonResp.asJsObject
          val JsString(accessToken) = fields("access_token")
          val JsNumber(expiresIn) = fields("expires_in")
          val refreshedUser = user.copy(accessToken = accessToken, expires = (System.currentTimeMillis / 1000 + expiresIn.toLong))

          saveUser(user)
          user.some.right
        }).recover {
          case x: Exception => SpotifyError.Thrown(x).left
          case x => SpotifyError.Unknown(s"During `userRefresh`: $x").left
        }
      }
      case None => Future.successful(None.right)
    }
  }

  /** Refresh client token.
    *
    * This doesn't exists, and is equal to `clientAuth`.
    */
  def clientRefresh(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): ResultF[ClientAuth] = clientAuth

  /** Authorize the client.
    *
    * Authorize the client and update the cache.
    */
  def clientAuth(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): ResultF[ClientAuth] = Result.async {
    (for {
      post <- Http().singleRequest(HttpRequest(
        POST,
        uri = tokenUri,
        headers = Seq(
          RawHeader("grant_type", "client_credentials"),
          RawHeader("client_id", srv.clientId),
          RawHeader("client_secret", srv.clientSecret))))
      jsonResp <- Unmarshal(post.entity).to[JsValue]
    } yield {
      val JsObject(fields) = jsonResp.asJsObject
      val JsString(accessToken) = fields("access_token")
      val JsNumber(expiresIn) = fields("expires_in")
      val client = ClientAuth(accessToken, expiresIn.toLong)

      saveClient(client)
      client.right
    }).recover {
      case x: Exception => SpotifyError.Thrown(x).left
      case x => SpotifyError.Unknown(s"During `clientAuth`: $x").left
    }
  }


  /* Authorize user using the authorization code.
   *
   * Make a POST request to the spotify "/api/token".
   * The `UserAuth` is saved in the cache with key `authCode`.
   */
  def userAuth(authCode: AuthCode)(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): ResultF[UserAuth] = Result.async {
    (for {
      post <- Http().singleRequest(HttpRequest(
        POST,
        uri = tokenUri,
        headers = Seq(
          RawHeader("grant_type", "authorization_code"),
          RawHeader("code", authCode),
          RawHeader("client_id", srv.clientId),
          RawHeader("client_secret", srv.clientSecret))))
      jsonResp <- Unmarshal(post.entity).to[JsValue]
    } yield {
      val JsObject(fields) = jsonResp.asJsObject
      val JsString(accessToken) = fields("access_token")
      val JsNumber(expiresIn) = fields("expires_in")
      val JsString(refreshToken) = fields("refresh_token")
      val user = UserAuth(authCode, accessToken, expiresIn.toLong, refreshToken)

      saveUser(user)
      user.right
    }).recover {
      case x: Exception => SpotifyError.Thrown(x).left
      case x => SpotifyError.Unknown(s"During `userAuth`: $x").left
    }
  }

  /** Get client authorization and refresh if expired. This requires that the client has authorized before. */
  def getClient(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): ResultF[ClientAuth] = {
    loadClient() match {
      case Some(client) if client.isExpired => clientRefresh
      case Some(client) => Result.okF(client)
      case None => clientAuth
    }
  }

  /* Get user and refresh as needed. This requires that the user has authorized before. */
  def getUser(authCode: String)(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): ResultF[Option[UserAuth]] = {
    loadUser(authCode) match {
      case Some(user) if user.isExpired => userRefresh(authCode)
      case Some(user) => Result.okF(user.some)
      case None => Result.okF(None)
    }
  }

}
