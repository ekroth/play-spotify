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
  import akka.http.scaladsl.model.headers.{ Authorization, OAuth2BearerToken }
  import akka.http.scaladsl.Http
  import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
  import akka.http.scaladsl.model.HttpRequest
  import akka.http.scaladsl.unmarshalling.Unmarshal
  import akka.stream.Materializer
  import spray.json._

  import errorhandling._

  private val baseUrl = "https://api.spotify.com/v1"
  val spotifyMaxOffset = Int.MaxValue
  val spotifyMaxLimit = 50

  /** Scopes for user access. */
  object Scopes {
    val playlistReadPrivate:   Scope = "playlist-read-private"
    val playlistModifyPublic:  Scope = "playlist-modify-public"
    val playlistModifyPrivate: Scope = "playlist-modify-private"
    val streaming:             Scope = "streaming"
    val userFollowModify:      Scope = "user-follow-modify"
    val userFollowRead:        Scope = "user-follow-read"
    val userLibraryRead:       Scope = "user-library-read"
    val userLibraryModify:     Scope = "user-library-modify"
    val userReadPrivate:       Scope = "user-read-private"
    val userReadBirthdate:     Scope = "user-read-birthdate"
    val userReadEmail:         Scope = "user-read-email"

    val all = Seq(
      playlistReadPrivate,
      playlistModifyPublic,
      playlistModifyPrivate,
      streaming,
      userFollowModify,
      userFollowRead,
      userLibraryRead,
      userLibraryModify,
      userReadPrivate,
      userReadBirthdate,
      userReadEmail
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
  def redirectUri(state: Option[String], scopes: Scope*)(implicit srv: Credentials): String = {
    val base = "https://accounts.spotify.com/authorize" +
    "?response_type=code" +
    s"&client_id=${srv.clientId}" +
    s"&redirect_uri=${srv.redirectUri}"

    val withState = state.map(s => s"&state=$s").getOrElse("")
    val withScopes = if (scopes.isEmpty) "" else scopes.mkString("&scope=", " ", "")

    base + withState + withScopes
  }

  private[spotify] def get[T : JsonFormat](url: String, token: Token, inner: Option[String])
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[T] = Result.okF {
    (for {
      resp <- Http().singleRequest(HttpRequest(
        uri = url,
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
      case x => SpotifyError.Unknown(s"During `wsOptUrl`: $x").left
     }
  }

  /** Get the current user's private profile. */
  def currentUserProfile(user: UserAuth)(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[UserPrivate] =
    get[UserPrivate](s"$baseUrl/me", user, None)

  /** Get the current user's liked tracks. */
  def currentUserTracks(user: UserAuth, limit: Int = spotifyMaxLimit)
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[Pager[SavedTrack]] = {
    requireBounds(1, limit, spotifyMaxLimit, "limit")
    get[Paging[SavedTrack]](s"$baseUrl/me/tracks?limit=$limit", user, None)
      .map(_.withExt())
  }

  def currentUserFollowedArtists(user: UserAuth, limit: Int = spotifyMaxLimit)
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[Pager[ArtistFull]] = {
    requireBounds(1, limit, spotifyMaxLimit, "limit")
    get[Paging[ArtistFull]](s"$baseUrl/me/following?type=artist&limit=$limit", user, Some("artists"))
      .map(_.withExt(Some("artists")))
  }

  def currentUserIsFollowing(user: UserAuth, ids: Seq[String])
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[Seq[(String, Boolean)]] = {
    requireBounds(1, ids.size, spotifyMaxLimit, "ids")

    get[Seq[Boolean]](s"$baseUrl/me/following/contains?type=artist&ids=${ids.mkString(",")}", user, None)
      .map(x => ids.zip(x))
  }

  def searchArtist(client: ClientAuth, query: String, limit: Int = spotifyMaxLimit)
    (implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext): ResultF[Pager[ArtistFull]] = {
    requireBounds(1, limit, spotifyMaxLimit, "limit")
    get[Paging[ArtistFull]](s"$baseUrl/search?type=artist&q=${query.escaped}", client, Some("artists"))
      .map(_.withExt(Some("artists")))
  }

  /** Refresh user token.
    *
    * This requires that the user has been authorized before and is
    * available in the Cache. The cache is refreshed if the user
    * authorization refresh is successful.
    */
val waaat = """  def userRefresh(authCode: AuthCode)(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): Future[Option[UserAuth]] = {
    loadUser(authCode) match {
      case Some(user) => {
        val post = WS.url("https://accounts.spotify.com/api/token").post(Map(
          "grant_type" -> Seq("refresh_token"),
          "refresh_token" -> Seq(user.refreshToken),
          "client_id" -> Seq(srv.clientId),
          "client_secret" -> Seq(srv.clientSecret)))

        post.map { resp =>
          val json = resp.json
          val refreshedUser = for {
            accessToken <- (json \ "access_token").validate[String]
            expiresIn <- (json \ "expires_in").validate[Int]
            if resp.status == OK
          } yield user.copy(accessToken = accessToken, expires = (System.currentTimeMillis / 1000 + expiresIn))

          refreshedUser.foreach(saveUser)
          refreshedUser.asOpt
        }.recover {
          case x => {
            logger.debug(s"userRefresh fail: '$x'")
            None
          }
        }

      }
      case None => Future.successful(None)
    }
  }

  /** Refresh client token.
    *
    * This doesn't exists, and is equal to `clientAuth`.
    */
  def clientRefresh(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): Future[Option[ClientAuth]] = clientAuth

  /** Authorize the client.
    *
    * Authorize the client and update the cache.
    */
  def clientAuth(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): Future[Option[ClientAuth]] = {
    val post = WS.url("https://accounts.spotify.com/api/token").post(Map(
      "grant_type" -> Seq("client_credentials"),
      "client_id" -> Seq(srv.clientId),
      "client_secret" -> Seq(srv.clientSecret)))

    post.map { resp =>
      val json = resp.json
      val client = for {
        accessToken <- (json \ "access_token").validate[String]
        expiresIn <- (json \ "expires_in").validate[Int]
        if resp.status == OK
      } yield ClientAuth(accessToken, expiresIn)

      client.foreach(saveClient)
      client.asOpt
    }.recover {
      case x => {
        logger.debug(s"clientAuth fail: '$x'")
        None
      }
    }
  }

  /* Authorize user using the authorization code.
   *
   * Make a POST request to the spotify "/api/token".
   * The `UserAuth` is saved in the cache with key `authCode`.
   */
  def userAuth(authCode: AuthCode)(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): Future[Option[UserAuth]] = {
    val post = WS.url("https://accounts.spotify.com/api/token").post(Map(
      "grant_type" -> Seq("authorization_code"),
      "code" -> Seq(authCode),
      "redirect_uri" -> Seq(srv.redirectUri),
      "client_id" -> Seq(srv.clientId),
      "client_secret" -> Seq(srv.clientSecret)))

    post.map { resp =>
      val json = resp.json
      val user = for {
        accessToken <- (json \ "access_token").validate[String]
        expiresIn <- (json \ "expires_in").validate[Int]
        refreshToken <- (json \ "refresh_token").validate[String]
        if resp.status == OK
      } yield UserAuth(authCode, accessToken, expiresIn, refreshToken)

      user.foreach(saveUser)
      user.asOpt
    }.recover {
      case x => {
        logger.debug(s"userAuth fail: '$x'")
        None
      }
    }
  }

  /** Get client authorization and refresh if expired. This requires that the client has authorized before. */
  def getClient(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): Future[Option[ClientAuth]] = {
    loadClient() match {
      case Some(client) if client.isExpired => clientRefresh
      case client: Some[_] => Future.successful(client)
      case None => clientAuth
    }
  }

  /* Get user and refresh as needed. This requires that the user has authorized before. */
  def getUser(authCode: String)(implicit sys: ActorSystem, fm: Materializer, ec: ExecutionContext, srv: Credentials): Future[Option[UserAuth]] = {
    loadUser(authCode) match {
      case Some(user) if user.isExpired => userRefresh(authCode)
      case user: Some[_] => Future.successful(user)
      case None => Future.successful(None)
    }
  }
"""

}
