package app.bootstrap

import app.domain._
import app.ports._
import cats.effect.Sync
import cats.syntax.flatMap._
import cats.syntax.functor._

import java.time.Instant

object SeedData {

  private def sample: (List[Group], List[Expense]) = {
    val g1 = Group(GroupId("g1"), "Group of dummies", List(UserId("u1"), UserId("u2"), UserId("u3")))
    val g2 = Group(GroupId("g2"), "Family", List(UserId("u10"), UserId("u11")))

    val e1 = Expense(
      id = ExpenseId("e1"),
      groupId = g1.id,
      payer = UserId("u1"),
      amount = BigDecimal(42.50),
      currency = "EUR",
      participants = List(UserId("u1"), UserId("u2")),
      createdAt = Instant.now(),
      note = Some("lunch")
    )

    val e2 = Expense(
      id = ExpenseId("e2"),
      groupId = g1.id,
      payer = UserId("u2"),
      amount = BigDecimal(15.00),
      currency = "EUR",
      participants = List(UserId("u1"), UserId("u2"), UserId("u3")),
      createdAt = Instant.now(),
      note = Some("coffee and water")
    )

    (List(g1, g2), List(e1, e2))
  }

  def load[F[_] : Sync](groupRepo: GroupRepo[F], expenseRepo: ExpenseRepo[F]): F[Unit] = {
    val (groups, expenses) = sample
    for {
      _ <- groups.foldLeft(Sync[F].unit)((acc, g) => acc >> groupRepo.create(g))
      _ <- expenses.foldLeft(Sync[F].unit)((acc, e) => acc >> expenseRepo.create(e))
    } yield ()
  }

  def loadIfEmpty[F[_] : Sync](groupRepo: GroupRepo[F], expenseRepo: ExpenseRepo[F]): F[Unit] =
    groupRepo.all.flatMap { gs =>
      if (gs.nonEmpty) Sync[F].unit
      else load(groupRepo, expenseRepo)
    }
}
