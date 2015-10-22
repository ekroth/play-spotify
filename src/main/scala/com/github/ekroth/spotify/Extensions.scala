/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth.spotify

trait Extensions {
  self: Commands =>

  import scala.collection.immutable.Seq
  import scala.concurrent._

  import play.api.Application
  import play.api.libs.json._
  import play.api.libs.iteratee._

  class Pager[T : Reads](val inner: Option[String], private val underlying: Paging[T]) {

    private[this] def fixUrl(x: String) = """users/[0-9]+""".r.replaceAllIn(x, "me")

    /** Some URLs should be /me and not /users/xyz, temporary fix. */
    def previousMeUrl: Option[String] = underlying.previous.map(fixUrl)

    /** See above. */
    def nextMeUrl: Option[String] = underlying.next.map(fixUrl)

    /** If page is the first one. */
    def isFirstPage: Boolean = underlying.previous.isEmpty

    /** If page is the last one. */
    def isLastPage: Boolean = underlying.next.isEmpty

    /** Previous underlying object. */
    def previousPage(token: Token)(implicit app: Application, ec: ExecutionContext): Future[Option[Pager[T]]] =
      previousMeUrl match {
        case Some(url) => wsOptUrl[Paging[T]](url, token, inner).map(_.map(_.withExt(inner)))
        case None => Future.successful(None)
      }

    /** Next underlying object. */
    def nextPage(token: Token)(implicit app: Application, ec: ExecutionContext): Future[Option[Pager[T]]] =
      nextMeUrl match {
        case Some(url) => wsOptUrl[Paging[T]](url, token, inner).map(_.map(_.withExt(inner)))
        case None => Future.successful(None)
      }

    /** This and the other pages.
      *
      * The enumerator will end at the last page or on auth/connection error.
      * The user can check if the last page is actually the last by checking
      * page.lastPage.
      */
    def allPages(token: Token)(implicit app: Application, ec: ExecutionContext): Enumerator[Pager[T]] =
      Enumerator(this) >>> Enumerator.unfoldM(this) { page =>
        page.nextPage(token).map { nextOpt =>
          nextOpt.map(next => (next, next))
        }
      }

    /** Current and all remaining items. */
    def allItems(token: Token)(implicit app: Application, ec: ExecutionContext): Future[Seq[T]] =
      allPages(token).through(Enumeratee.map(_.underlying.items)).run(Iteratee.consume[Seq[T]]())
  }

  implicit final class RichPaging[T : Reads](private val underlying: Paging[T]) {
    def withExt(inner: Option[String] = None): Pager[T] = new Pager(inner, underlying)
  }
}
