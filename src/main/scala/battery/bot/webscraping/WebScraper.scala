package battery.bot.webscraping

import cats.effect.IO
import doobie.implicits._
import org.jsoup.Jsoup
import java.time._

object Scraper {
  val doc = Jsoup.connect("https://tarifaluzhora.es")

  def scraperPrice: IO[List[(Instant, BigDecimal)]] = IO {
    val priceList = doc
      .get()
      .getElementsByAttributeValue("itemprop", "price")
      .text
      .split("€/kWh")
      .toList
      .map(_.trim)
      .map(BigDecimal(_))

    val dateList = (0 to 23).toList.map(hour =>
      LocalDateTime.of(LocalDate.now(), LocalTime.of(hour, 0)).atZone(ZoneId.of("Europe/Madrid")).toInstant
    )
    dateList.zip(priceList)
  }
}
