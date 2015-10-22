/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth

package object spotify extends Objects {

  import scala.collection.immutable.Seq

  /* Auth */
  sealed trait Token {
    def expires: Long
    val margin: Int = 100
    def accessToken: String
    def expiresIn: Int = (expires - System.currentTimeMillis / 1000).asInstanceOf[Int]
    def isExpired: Boolean = System.currentTimeMillis / 1000 >= expires
  }

  /* Authorization and credentials. */
  case class Credentials(redirectUri: String, clientId: String, clientSecret: String)
  case class ClientAuth(accessToken: AccessToken, expires: Long) extends Token
  case class UserAuth(oauth: String, accessToken: AccessToken, expires: Long, refreshToken: String) extends Token

  import errorhandling._

  object SpotifyError {
    case class Permission(reason: String) extends Error
    case class Json(error: play.api.libs.json.JsError, reason: String = "") extends Error
    case class Impl(reason: String) extends Error
    case class Usage(error: ErrorMessage) extends Error {
      override def reason = error.toString
    }
  }

  /** Bounds requirement.
    *
    * Requires `lower` <= `actual` <= `upper`.
    */
  final def requireBounds[T : Ordering](lower: T, actual: T, upper: T, msg: String): Unit = {
    val ord = implicitly[Ordering[T]]
    require(ord.lteq(actual, upper) && ord.gteq(actual, lower), s"'$msg' variable require bounds [$lower, $upper], actual $actual")
  }

  import scala.concurrent.{ ExecutionContext, Future }

  final implicit class RichFuture(val underlying: Future.type) {

    def unfold[T](start: => T)(op: T => Future[Option[T]])(implicit ec: ExecutionContext): Future[Seq[T]] =
      op(start).flatMap { pageOpt =>
        pageOpt match {
          case Some(x) => unfold(x)(op).map(y => Seq(x) ++ y)
          case None => Future.successful(Seq.empty)
        }
      }
  }
}
