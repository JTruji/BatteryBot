package webscraping

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.time.LocalDate

  object JsoupHtmlToPlainTextTest extends App {
    def scraperTime = {
  val doc: Document = Jsoup.connect("https://tarifaluzhora.es").get
  val s: String = doc.body.text()
  doc
    .getElementsByAttributeValue("itemprop", "description")
    .text
    .split(":")
    .toList
    .map(_.trim)
    .mkString("\n")
}
    def scraperPrice = {
      val doc: Document = Jsoup.connect("https://tarifaluzhora.es").get
      val s: String = doc.body.text()
      doc.getElementsByAttributeValue("itemprop", "price")
        .text
        .split("€/kWh")
        .toList
        .map(_.trim)
        .mkString("\n")
    }
  }