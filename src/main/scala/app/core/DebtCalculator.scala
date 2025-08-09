package app.core

import app.domain.{Expense, Group, Settlement, UserId}

object DebtCalculator {

  def calculate(group: Group, expenses: List[Expense]): List[Settlement] = {
    if (expenses.isEmpty) return Nil

    //assumption that all expenses are in the same currency.
    //in future, adapt logic to allow expenses with different currencies
    val currency = expenses.head.currency

    val memberSet = group.members.toSet
    val zeros = memberSet.map(_ -> BigDecimal(0)).toMap

    val netAfterPaid = expenses.foldLeft(zeros) { (acc, e) =>
      val payerInc = acc.updated(e.payer, acc.getOrElse(e.payer, BigDecimal(0)) + e.amount)
      payerInc
    }

    val net = expenses.foldLeft(netAfterPaid) { (acc, e) =>
      val share = e.amount / BigDecimal(e.participants.size)
      e.participants.foldLeft(acc) { (acc2, p) =>
        acc2.updated(p, acc2.getOrElse(p, BigDecimal(0)) - share)
      }
    }

    val (debtors, creditors) = net.toList.foldLeft((List.empty[(UserId, BigDecimal)], List.empty[(UserId, BigDecimal)])) {
      case ((ds, cs), (u, amt)) =>
        if (amt < 0) ((u, -amt) :: ds, cs)
        else if (amt > 0) (ds, (u, amt) :: cs)
        else (ds, cs)
    }

    val result = scala.collection.mutable.ListBuffer.empty[Settlement]
    val ds = scala.collection.mutable.PriorityQueue(debtors: _*)(Ordering.by[(UserId, BigDecimal), BigDecimal](_._2))
    val cs = scala.collection.mutable.PriorityQueue(creditors: _*)(Ordering.by[(UserId, BigDecimal), BigDecimal](_._2))

    while (ds.nonEmpty && cs.nonEmpty) {
      val (debtor, dAmt) = ds.dequeue()
      val (creditor, cAmt) = cs.dequeue()
      val pay = dAmt.min(cAmt)

      if (pay > 0) result += Settlement(debtor, creditor, pay, currency)

      val dR = dAmt - pay
      val cR = cAmt - pay
      if (dR > 0) ds.enqueue((debtor, dR))
      if (cR > 0) cs.enqueue((creditor, cR))
    }

    result.toList
  }

}
