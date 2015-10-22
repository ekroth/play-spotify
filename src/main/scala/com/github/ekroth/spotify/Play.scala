/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth.spotify

trait PlayCommands {
  self: Commands =>

  import scala.collection.immutable.Seq
  import scala.concurrent.{ Future, ExecutionContext }

  import play.api._
  import play.api.mvc._
  import play.api.libs.ws._
  import play.api.libs.json._

  /** Supply `block` with current user.
    *
    * The auth_code is read from the session, `spotify.Commands.getUser` is
    * used to access the user and refresh as needed.
    *
    * `unathorized` is returned if `spotify.Commands.getUser` fails.
    * `redirect` is returned if session is missing auth code.
    * Otherwise the return value of parameter `block` is returned.
    *
    * @see `withUserAsync` for use with `Future`.
    */
  def withUser(fallback: => Result)(block: UserAuth => Result)(implicit
    app: Application,
    ec: ExecutionContext,
    srv: Credentials,
    request: Request[AnyContent]): Future[Result] = {

    request.session.get("auth_code").map { authCode =>
      getUser(authCode).map { userOpt =>
        userOpt.map(block).getOrElse(fallback)
      }
    }.getOrElse(Future.successful(fallback))
  }

  def withUserEitherAsync[T](fallback: => Result)(block: UserAuth => Future[Either[Result, T]])(implicit
    app: Application,
    ec: ExecutionContext,
    srv: Credentials,
    request: RequestHeader): Future[Either[Result, T]] = {

    request.session.get("auth_code").map { authCode =>
      getUser(authCode).flatMap { userOpt =>
        userOpt.map(block).getOrElse(Future.successful(Left(fallback)))
      }
    }.getOrElse(Future.successful(Left(fallback)))
  }


  /** Supply `block` with current user.
    *
    * The auth_code is read from the session, `spotify.Commands.getUser` is
    * used to access the user and refresh as needed.
    *
    * `unathorized` is returned if `spotify.Commands.getUser` fails.
    * `redirect` is returned if session is missing auth code.
    * Otherwise the return value of parameter `block` is returned.
    *
    * @see `withUser` for use without `Future`.
    */
  def withUserAsync(fallback: => Result)(block: UserAuth => Future[Result])(implicit
    app: Application,
    ec: ExecutionContext,
    srv: Credentials,
    request: Request[AnyContent]): Future[Result] = {

    request.session.get("auth_code").map { authCode =>
      getUser(authCode).flatMap { userOpt =>
        userOpt.map(block).getOrElse(Future.successful(fallback))
      }
    }.getOrElse(Future.successful(fallback))
  }

  /** Supply `block` with client.
    *
    * `unathorized` is returned if `spotify.Commands.getClient` fails.
    * `redirect` is returned if session is missing auth code.
    * Otherwise the return value of parameter `block` is returned.
    *
    * @see `withUser` for use without `Future`.
    */
  def withClientAsync(fallback: => Result)(block: ClientAuth => Future[Result])(implicit
    app: Application,
    ec: ExecutionContext,
    srv: Credentials,
    request: Request[AnyContent]): Future[Result] = {

    getClient.flatMap { clientOpt =>
      clientOpt.map(block).getOrElse(Future.successful(fallback))
    }
  }
}
