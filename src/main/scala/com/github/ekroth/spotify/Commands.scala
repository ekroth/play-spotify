/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth.spotify

/** Commands corresponding to the Spotify Web API v1. */
trait Commands {
  self: Caching =>

  import scala.concurrent.Future
  import scala.concurrent.ExecutionContext

  import play.api.Logger
  import play.api.Application
  import play.api.http.Status._
  import play.api.libs.ws._
  import play.api.libs.json._

  /** Scopes for user access. */
  object Scopes {
    val playlistReadPrivate: Scope = "playlist-read-private"
    val playlistModifyPublic: Scope = "playlist-modify-public"
    val playlistModifyPrivate: Scope = "playlist-modify-private"
    val streaming: Scope = "streaming"
    val userFollowModify: Scope = "user-follow-modify"
    val userFollowRead: Scope = "user-follow-read"
    val userLibraryRead: Scope = "user-library-read"
    val userLibraryModify: Scope = "user-library-modify"
    val userReadPrivate: Scope = "user-read-private"
    val userReadBirthdate: Scope = "user-read-birthdate"
    val userReadEmail: Scope = "user-read-email"

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

  private[this] lazy val logger = Logger("spotify")

  /** Create a redirect URL.
    *
    * The server generates a state variable, selects some scopes and
    * redirects the user to this URL. The user is then redirected back
    * to the redirect URL set in `spotify.Credentials`.
    *
    * @param state Optional state variable.
    * @param scopes Scopes.
    */
  def redirectUri(state: Option[String], scopes: Scope*)(implicit srv: Credentials) = {
    val base = "https://accounts.spotify.com/authorize" +
    "?response_type=code" +
    s"&client_id=${srv.clientId}" +
    s"""&redirect_uri=${srv.redirectUri}"""

    val withState = state.map(s => s"&state=$s").getOrElse("")
    val withScopes = if (scopes.isEmpty) "" else scopes.mkString("&scope=", " ", "")

    base + withState + withScopes
  }

  private[spotify] def wsOptUrl[T : Reads](url: String, user: UserAuth)(implicit app: Application, ec: ExecutionContext): Future[Option[T]] =
    WS.url(url)
      .withHeaders("Authorization" -> s"Bearer ${user.accessToken}").get().map { resp =>
      if (resp.status == OK) {
        logger.trace(s"""'$url' ok: '${resp.json}'""")
        resp.json.validate[T] match {
          case JsError(errs) => {
            logger.error(s"""'$url' json errors: '$errs'""")
            None
          }
          case JsSuccess(res, _) => Some(res)
        }
      } else {
        logger.debug(s"""'$url' fail: '$resp'""")
        None
      }
    }.recover {
      case x => {
        logger.debug(s"""'$url' fail: '$x'""")
        None
      }
    }

  /** Get the current user's private profile. */
  def currentUserProfile(user: UserAuth)(implicit app: Application, ec: ExecutionContext): Future[Option[UserPrivate]] =
    wsOptUrl[UserPrivate](s"https://api.spotify.com/v1/me", user)

  /** Get the current user's liked tracks. */
  def currentUserTracks(user: UserAuth, offset: Int = 0, limit: Int = 50)(implicit app: Application, ec: ExecutionContext): Future[Option[Paging[SavedTrack]]] = {
    requireBounds(0, limit, 50, "limit")
    requireBounds(0, offset, Int.MaxValue, "offset")
    wsOptUrl[Paging[SavedTrack]](s"https://api.spotify.com/v1/me/tracks?offset=$offset&limit=$limit", user)
  }

  /** Refresh user token.
    *
    * This requires that the user has been authorized before and is
    * available in the Cache. The cache is refreshed if the user
    * authorization refresh is successful.
    */
  def userRefresh(authCode: AuthCode)(implicit app: Application, ec: ExecutionContext, srv: Credentials): Future[Option[UserAuth]] = {
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
            logger.debug(s"""userRefresh fail: '$x'""")
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
  def clientRefresh(implicit app: Application, ec: ExecutionContext, srv: Credentials) = clientAuth

  /** Authorize the client.
    *
    * Authorize the client and update the cache.
    */
  def clientAuth(implicit app: Application, ec: ExecutionContext, srv: Credentials): Future[Option[ClientAuth]] = {
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
        logger.debug(s"""clientAuth fail: '$x'""")
        None
      }
    }
  }

  /* Authorize user using the authorization code.
   *
   * Make a POST request to the spotify "/api/token".
   * The `UserAuth` is saved in the cache with key `authCode`.
   */
  def userAuth(authCode: AuthCode)(implicit app: Application, ec: ExecutionContext, srv: Credentials): Future[Option[UserAuth]] = {
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
        logger.debug(s"""userAuth fail: '$x'""")
        None
      }
    }
  }

  /** Get client authorization and refresh if expired. This requires that the client has authorized before. */
  def getClient(implicit app: Application, ec: ExecutionContext, srv: Credentials): Future[Option[ClientAuth]] = {
    loadClient() match {
      case Some(client) if client.isExpired => clientRefresh
      case client: Some[_] => Future.successful(client)
      case None => Future.successful(None)
    }
  }

  /* Get user and refresh as needed. This requires that the user has authorized before. */
  def getUser(authCode: String)(implicit app: Application, ec: ExecutionContext, srv: Credentials): Future[Option[UserAuth]] = {
    loadUser(authCode) match {
      case Some(user) if user.isExpired => userRefresh(authCode)
      case user: Some[_] => Future.successful(user)
      case None => Future.successful(None)
    }
  }
}
