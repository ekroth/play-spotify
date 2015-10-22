/* Copyright (c) 2015 Andrée Ekroth.
 * Distributed under the MIT License (MIT).
 * See accompanying file LICENSE or copy at
 * http://opensource.org/licenses/MIT
 */

package com.github.ekroth
package spotify

trait Extensions {
  self: Commands =>

  import scala.collection.immutable.Seq
  import scala.concurrent._

  import scalaz._
  import Scalaz._
  import scalaz.contrib._
  import scalaz.contrib.std._

  import play.api.Application
  import play.api.libs.json._
  import play.api.libs.iteratee._

  import errorhandling._

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
    def previousPage(token: Token)(implicit app: Application, ec: ExecutionContext): ResultF[Option[Pager[T]]] =
      previousMeUrl match {
        case Some(url) => wsOptUrl[Paging[T]](url, token, inner).map(x => Some(x.withExt(inner)))
        case None => Result.okF(Future.successful(None.right))
      }

    /** Next underlying object. */
    def nextPage(token: Token)(implicit app: Application, ec: ExecutionContext): ResultF[Option[Pager[T]]] =
      nextMeUrl match {
        case Some(url) => wsOptUrl[Paging[T]](url, token, inner).map(x => Some(x.withExt(inner)))
        case None => Result.okF(Future.successful(None.right))
      }


    /* Retrieve this and all remaining pages. */
    def allPages(token: Token)(implicit app: Application, ec: ExecutionContext): Future[Seq[Result[Pager[T]]]] = {
      Future.unfold(Result.ok(this)) { pageResult =>
        pageResult match {
          case \/-(pager) if pager.isLastPage => Future.successful(None)
          case \/-(pager) => pager.nextPage(token).run.map { pageResult2 =>
            pageResult2 match {
              case \/-(x) => x.map(_.right)
              case -\/(_) => None
            }
          }
          case -\/(_) => Future.successful(None)
        }
      }
    }

    /* Retrieve this page's items and all remaining items. */
    def allItems(token: Token)(implicit app: Application, ec: ExecutionContext): ResultF[Seq[T]] = Result.okF {
      val pages = allPages(token).map(Result.sequence(_))
      pages.map { pageResult =>
        pageResult.map { page =>
          page.flatMap(_.underlying.items)
        }
      }
    }

  }

  implicit final class RichPaging[T : Reads](private val underlying: Paging[T]) {
    def withExt(inner: Option[String] = None): Pager[T] = new Pager(inner, underlying)
  }
}
