package app.http

import app.domain.{ExpenseId, GroupId, Settlement, UserId}
import io.circe.generic.semiauto.{deriveDecoder, deriveEncoder}
import io.circe.{Decoder, Encoder}

import java.time.Instant

object JsonCodecs {
  implicit val encGroupId: Encoder[GroupId] = Encoder.encodeString.contramap(_.value)
  implicit val decGroupId: Decoder[GroupId] = Decoder.decodeString.map(GroupId)

  implicit val encUserId: Encoder[UserId] = Encoder.encodeString.contramap(_.value)
  implicit val decUserId: Decoder[UserId] = Decoder.decodeString.map(UserId)

  implicit val encExpenseId: Encoder[ExpenseId] = Encoder.encodeString.contramap(_.value)
  implicit val decExpenseId: Decoder[ExpenseId] = Decoder.decodeString.map(ExpenseId)

  implicit val encInstant: Encoder[Instant] = Encoder.encodeString.contramap(_.toString)
  implicit val decInstant: Decoder[Instant] =
    Decoder.decodeString.emap(s =>
      scala.util.Try(Instant.parse(s)).toEither.left.map(_.getMessage)
    )

  implicit val encSettlement: io.circe.Encoder[Settlement] = deriveEncoder
  implicit val decSettlement: io.circe.Decoder[Settlement] = deriveDecoder

}
