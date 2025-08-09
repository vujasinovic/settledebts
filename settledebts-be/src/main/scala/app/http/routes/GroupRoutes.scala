package app.http.routes

import app.domain.Group
import app.ports.GroupRepo
import cats.effect.IO
import io.circe.generic.auto._
import io.circe.syntax.EncoderOps
import org.http4s.circe.CirceEntityCodec._
import org.http4s.circe.{jsonEncoderOf, jsonOf}
import org.http4s.dsl.io._
import org.http4s.{EntityDecoder, EntityEncoder, HttpRoutes}

final class GroupRoutes(groupRepo: GroupRepo[IO]) {

  implicit val encGroups: EntityEncoder[IO, List[Group]] = jsonEncoderOf[IO, List[Group]]
  implicit val decGroup: EntityDecoder[IO, Group] = jsonOf[IO, Group]

  val routes: HttpRoutes[IO] = HttpRoutes.of[IO] {
    case GET -> Root / "api" / "groups" =>
      groupRepo.all.flatMap(Ok(_))

    case req@POST -> Root / "api" / "groups" =>
      req.attemptAs[Group].value.flatMap {
        case Right(g) =>
          groupRepo.create(g) *> Created(Map("status" -> "Resource saved").asJson)
        case Left(df) =>
          BadRequest(Map("error" -> s"Invalid body: ${df.getMessage}").asJson)
      }
  }
}
