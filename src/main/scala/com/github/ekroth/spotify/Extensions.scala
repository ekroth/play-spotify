package com.github.ekroth.spotify

trait Extensions {
  self: Commands =>

  import scala.concurrent._

  import play.api.Application
  import play.api.libs.json._
  import play.api.libs.iteratee._

  implicit final class RichPaging[T : Reads](private val underlying: Paging[T]) {

    /** If page is the first one. */
    def isFirstPage: Boolean = underlying.previous.isEmpty

    /** If page is the last one. */
    def isLastPage: Boolean = underlying.next.isEmpty

    /** Previous underlying object. */
    def previousPage(user: UserAuth)(implicit app: Application, ec: ExecutionContext): Future[Option[Paging[T]]] =
      underlying.previous match {
        case Some(url) => wsOptUrl[Paging[T]](url, user)
        case None => Future.successful(None)
      }

    /** Next underlying object. */
    def nextPage(user: UserAuth)(implicit app: Application, ec: ExecutionContext): Future[Option[Paging[T]]] =
      underlying.next match {
        case Some(url) => wsOptUrl[Paging[T]](url, user)
        case None => Future.successful(None)
      }

    /** This and the other pages.
      *
      * The enumerator will end at the last page or on auth/connection error.
      * The user can check if the last page is actually the last by checking
      * page.lastPage.
      */
    def allPages(user: UserAuth)(implicit app: Application, ec: ExecutionContext): Enumerator[Paging[T]] =
      Enumerator(underlying) >>> Enumerator.unfoldM(underlying) { page =>
        page.nextPage(user).map { nextOpt =>
          nextOpt.map { next =>
            (next, next)
          }
        }
      }

    /** Current and all remaining items. */
    def allItems(user: UserAuth)(implicit app: Application, ec: ExecutionContext): Future[Seq[T]] =
      allPages(user).through(Enumeratee.map { _.items }).run(Iteratee.consume[Seq[T]]())
  }
}
