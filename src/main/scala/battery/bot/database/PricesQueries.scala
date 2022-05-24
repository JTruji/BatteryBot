package battery.bot.database

import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update
import doobie.util.update.Update
import doobie._
import doobie.implicits._
import doobie.util.ExecutionContexts
import cats._
import cats.data._
import cats.effect._
import cats.implicits._

import java.time.Instant

object PricesQueries {
  def insertPrices(timeRange: Instant, price: BigDecimal): doobie.Update0 =
    sql"""insert into prices (time_range, price) values ($timeRange, $price)""".update
//  val insertListPrices: Update[(Instant, BigDecimal)] =
//    Update[(Instant, BigDecimal)]("""insert into prices (time_range, price) values (?, ?)""")

  val insertManyPrices: Update[(Instant, BigDecimal)] =
    Update[(Instant, BigDecimal)]("insert into prices (time_range, price) values (?, ?)")

}
