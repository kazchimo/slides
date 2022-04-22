
implicit def plus1IntToString(i: Int): String = (i + 1).toString

val two: String = 1
two

implicit class RichInt(i: Int) {
  def doubled: Int = i * 2
}

2.doubled



import scala.concurrent.{Await, ExecutionContext, Future}

def asyncDouble(f: Future[Int])(implicit ec: ExecutionContext): Future[Int] = f.map(_ * 2)

import scala.concurrent.ExecutionContext.global
import scala.concurrent.duration.Duration

implicit val ec: ExecutionContext = global

Await.result(asyncDouble(Future(2)), Duration.Inf)

implicitly[ExecutionContext]


object implicits {

}
