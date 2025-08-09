package app.adapters.memory

import app.domain._
import app.ports._
import cats.effect.{Ref, Sync}
import cats.implicits.toFunctorOps

final class InMemoryGroupRepo[F[_] : Sync](state: Ref[F, Map[GroupId, Group]]) extends GroupRepo[F] {
  def all = state.get.map(_.values.toList)

  def byId(id: GroupId) = state.get.map(_.get(id))

  def create(g: Group) = state.update(_ + (g.id -> g))
}

object InMemoryGroupRepo {
  def create[F[_] : Sync]: F[InMemoryGroupRepo[F]] =
    Ref.of[F, Map[GroupId, Group]](Map.empty).map(new InMemoryGroupRepo[F](_))
}

final class InMemoryExpenseRepo[F[_] : Sync](state: Ref[F, Map[GroupId, List[Expense]]]) extends ExpenseRepo[F] {
  def byGroup(groupId: GroupId) = state.get.map(_.getOrElse(groupId, Nil))

  def create(e: Expense) = state.update { m =>
    val lst = m.getOrElse(e.groupId, Nil)
    m.updated(e.groupId, e :: lst)
  }
}

object InMemoryExpenseRepo {
  def create[F[_] : Sync]: F[InMemoryExpenseRepo[F]] =
    Ref.of[F, Map[GroupId, List[Expense]]](Map.empty).map(new InMemoryExpenseRepo[F](_))
}
