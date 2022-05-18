package database

import doobie.implicits._
import doobie.postgres.implicits._

import java.time.Instant

object PricesQueries {
  def insertPrices(timeRange: Instant, price: BigDecimal): doobie.Update0 =
    sql"""insert into prices (time_range, price) values ($timeRange, $price)""".update
}
