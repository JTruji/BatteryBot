package database

import doobie.implicits._

import java.time.Instant

object PricesQueries {
  def insertPrices(time_range: Instant, price: BigDecimal): doobie.Update0 =
    sql"""insert into prices (time_range, price) values ($time_range, $price)""".update
}
