package battery.bot.webscraping

import cats.effect.IO
import org.jsoup.Jsoup

object Scraper {
  val doc = Jsoup.connect("https://tarifaluzhora.es")
  def scraperTime = IO {
    doc
      .get()
      .getElementsByAttributeValue("itemprop", "description")
      .text
      .split(":")
      .toList
      .map(_.trim)
  }
  def scraperPrice = IO {
    doc
      .get()
      .getElementsByAttributeValue("itemprop", "price")
      .text
      .split("€/kWh")
      .toList
      .map(_.trim)
  }
}
