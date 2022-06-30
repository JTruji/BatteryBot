package battery.bot.database

import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.update.Update

import java.time.{Instant, LocalDateTime}

object PricesQueries {
  val insertManyPrices: Update[(Instant, BigDecimal)] =
    Update[(Instant, BigDecimal)]("insert into prices (time_range, price) values (?, ?) on conflict do nothing")

  def pricesTimeTrue(time: Instant): doobie.Query0[BigDecimal] =
    sql"""select price from prices where time_range > $time"""
      .query[BigDecimal]

  def pricesTimeFalse(time: Instant, weakUpTime: Instant, sleepingTime: Instant): doobie.Query0[BigDecimal] = {
    println(s"$weakUpTime , $sleepingTime")
    sql"""select price from prices where time_range between $weakUpTime and $sleepingTime and time_range > $time """
      .query[BigDecimal]
  }

  def lowerPriceTime(price: BigDecimal, time: Instant): doobie.Query0[LocalDateTime] =
    sql"""select time_range from prices where price = $price limit 1""".query[LocalDateTime]
}
