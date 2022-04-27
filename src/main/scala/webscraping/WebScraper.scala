package webscraping

import org.jsoup.Jsoup
import org.jsoup.nodes.Document

import java.time.LocalDate

  object JsoupHtmlToPlainTextTest extends App {

    val doc: Document = Jsoup.connect("https://tarifaluzhora.es").get
    val s: String = doc.body.text()

    val date: String = LocalDate.now().toString

    val time= doc.getElementsByAttributeValue("itemprop", "description").text.split(":").toList.map(_.trim).mkString("\n")
    val price = doc.getElementsByAttributeValue("itemprop", "price").text.split("€/kWh").toList.map(_.trim).mkString("\n")

    println(date)
    println(time)
    println(price)
  }