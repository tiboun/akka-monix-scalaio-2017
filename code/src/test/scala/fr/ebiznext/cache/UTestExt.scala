package fr.ebiznext.cache

import scala.concurrent.Future

trait UTestExt {
  def recoverToSucceededIf[T <: Throwable: Manifest](
      f: => Future[Any]): Future[Any] = {
    import scala.concurrent.ExecutionContext.Implicits.global
    f.map[Either[Throwable, Throwable]](_ => Left(new RuntimeException("no exception occured"))).recover {
      case e: T => Right(e)
      case e    => Left(e)
    } map (e => {
      assert(e.isRight,
        e.left.get.getClass.getName + " has been thrown instead of " + manifest[T].runtimeClass.getName)
    })
  }
}
