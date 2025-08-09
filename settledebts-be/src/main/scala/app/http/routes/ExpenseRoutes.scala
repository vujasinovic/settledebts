package app.http.routes

import app.domain._
import app.ports._
import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._

import java.time.Instant

class ExpenseRoutes(expenseRepo: ExpenseRepo[IO]) {
  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "api" / "groups" / gid / "expenses" =>
      expenseRepo.byGroup(GroupId(gid)).flatMap(Ok(_))

    case req@POST -> Root / "api" / "groups" / gid / "expenses" =>
      req.as[Expense].attempt.flatMap {
        case Right(e) =>
          val normalized = e.copy(
            groupId = GroupId(gid),
            createdAt = Option(e.createdAt).getOrElse(Instant.now())
          )
          expenseRepo.create(normalized) *> Created(Map("status" -> "Resource saved").asJson)
        case Left(err) =>
          BadRequest(Map("error" -> s"Invalid body: ${err.getMessage}").asJson)
      }
  }
}
