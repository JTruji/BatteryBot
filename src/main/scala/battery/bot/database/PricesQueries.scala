package battery.bot.database

import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update

import java.math.BigDecimal
import java.time.Instant

object PricesQueries {
  def insertPrices(timeRange: Instant, price: BigDecimal): doobie.Update0 =
    sql"""insert into prices (time_range, price) values ($timeRange, $price)""".update

  val insertManyPrices: Update[(Instant, BigDecimal)] =
    Update[(Instant, BigDecimal)]("insert into prices (time_range, price) values (?, ?)")

}
