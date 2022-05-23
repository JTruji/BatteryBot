package battery.bot.webscraping

import cats.effect.IO
import org.jsoup.Jsoup

object Scraper {
  val doc = Jsoup.connect("https://tarifaluzhora.es")
  def scraperTime: IO[List[String]] = IO {
    doc
      .get()
      .getElementsByAttributeValue("itemprop", "description")
      .text
      .split("h: ")
      .toList
      .map(_.trim)
      .map(_.take(2))
  }
  def scraperPrice: IO[List[String]] = IO {
    doc
      .get()
      .getElementsByAttributeValue("itemprop", "price")
      .text
      .split("€/kWh")
      .toList
      .map(_.trim)
  }
}
