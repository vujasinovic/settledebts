package app


import app.adapters.memory._
import app.bootstrap.SeedData
import app.http.routes.{ExpenseRoutes, GroupRoutes, SettlementRoutes}
import cats.effect._
import cats.implicits.toSemigroupKOps
import com.comcast.ip4s._
import org.http4s.ember.server._
import org.http4s.server.Router

object Main extends IOApp.Simple {
  val run: IO[Unit] = for {
    groupRepo <- InMemoryGroupRepo.create[IO]
    expenseRepo <- InMemoryExpenseRepo.create[IO]

    _ <- SeedData.loadIfEmpty[IO](groupRepo, expenseRepo)

    httpApp = Router(
      "/" -> (
        new GroupRoutes(groupRepo).routes <+>
          new ExpenseRoutes(expenseRepo).routes <+>
          new SettlementRoutes(groupRepo, expenseRepo).routes
        )
    ).orNotFound

    _ <- EmberServerBuilder
      .default[IO]
      .withHost(ipv4"0.0.0.0")
      .withPort(port"8080")
      .withHttpApp(httpApp)
      .build
      .useForever
  } yield ()

}
