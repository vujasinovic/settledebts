package app.http.routes

import app.core.DebtCalculator
import app.domain._
import app.ports._
import cats.effect.IO
import io.circe.syntax._
import org.http4s._
import org.http4s.circe.CirceEntityCodec._
import org.http4s.dsl.io._

final class SettlementRoutes(groupRepo: GroupRepo[IO], expenseRepo: ExpenseRepo[IO]) {

  import app.http.JsonCodecs._

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "api" / "groups" / gid / "settlements" =>
      (for {
        groupOpt <- groupRepo.byId(GroupId(gid))
        group <- IO.fromOption(groupOpt)(new RuntimeException(s"Group $gid not found"))
        expenses <- expenseRepo.byGroup(group.id)
        settles = DebtCalculator.calculate(group, expenses)
      } yield settles).attempt.flatMap {
        case Right(list) => Ok(list.asJson)
        case Left(_: NoSuchElementException) | Left(_: RuntimeException) =>
          NotFound(Map("error" -> s"Group $gid not found").asJson)
        case Left(err) =>
          InternalServerError(Map("error" -> err.getMessage).asJson)
      }
  }
}