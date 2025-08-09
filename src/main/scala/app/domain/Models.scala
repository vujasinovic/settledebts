package app.domain

import java.time.Instant

final case class GroupId(value: String) extends AnyVal

final case class UserId(value: String) extends AnyVal

final case class ExpenseId(value: String) extends AnyVal

final case class Group(id: GroupId, name: String, members: List[UserId])

final case class Expense(
                          id: ExpenseId,
                          groupId: GroupId,
                          payer: UserId,
                          amount: BigDecimal,
                          currency: String,
                          participants: List[UserId],
                          createdAt: Instant,
                          note: Option[String]
                        )
