package app.domain

final case class Settlement(from: UserId, to: UserId, amount: BigDecimal, currency: String)
