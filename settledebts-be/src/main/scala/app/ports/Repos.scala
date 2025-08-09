package app.ports

import app.domain.{Expense, Group, GroupId}

trait GroupRepo[F[_]] {
  def all: F[List[Group]]

  def byId(id: GroupId): F[Option[Group]]

  def create(g: Group): F[Unit]
}

trait ExpenseRepo[F[_]] {
  def byGroup(groupId: GroupId): F[List[Expense]]

  def create(e: Expense): F[Unit]
}
