package battery.bot.database

import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update

import java.time.{Instant, LocalDateTime}

object PricesQueries {
  val insertManyPrices: Update[(Instant, BigDecimal)] =
    Update[(Instant, BigDecimal)]("insert into prices (time_range, price) values (?, ?) on conflict do nothing")

  def pricesTime(time: Instant): doobie.Query0[BigDecimal] =
    sql"""select price from prices where time_range > $time"""
      .query[BigDecimal]

  def getTime(price: BigDecimal, time: Instant): doobie.Query0[LocalDateTime] =
    sql"""select time_range from prices where price = $price""".query[LocalDateTime]
}
