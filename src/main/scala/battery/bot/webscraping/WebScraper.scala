package battery.bot.webscraping

import battery.bot.database.PricesQueries
import cats.{Foldable, Semigroup, Traverse}
import cats.effect.IO
import org.jsoup.Jsoup
import doobie._
import doobie.implicits._
import doobie.util._
import cats._
import cats.data._
import cats.effect._
import cats.implicits._

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, ZoneId}

object Scraper{
  val doc = Jsoup.connect("https://tarifaluzhora.es")
//  def scraperTime: List[String] = {
//    doc
//      .get()
//      .getElementsByAttributeValue("itemprop", "description")
//      .text
//      .split("h: ")
//      .toList
//      .map(_.trim)
//      .map(_.take(2))
//  }
  def scraperPrice: List[BigDecimal] = {
    doc
      .get()
      .getElementsByAttributeValue("itemprop", "price")
      .text
      .split("€/kWh")
      .toList
      .map(_.trim)
      .map(_.toFloat)
  }

  val dateList = (0 to 23).toList.map(hour =>
    LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, 0)).atZone(ZoneId.of("Europe/Madrid")).toInstant
  )

  val priceList = dateList.zip(scraperPrice)
//println(priceList)
//  PricesQueries.insertManyPrices(scraperPrice.zipWithIndex.map(_.swap))
//  PricesQueries.insertManyPrices(priceList)

}
