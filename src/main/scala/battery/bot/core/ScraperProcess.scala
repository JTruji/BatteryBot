package battery.bot.core

import battery.bot.webscraping.Scraper.priceList

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, ZoneId}

class ScraperProcess {
  def getNewPrices: Unit = {
    val isPriceUpdated = priceList.map(_._1).head
    val fecha = LocalDateTime.of(LocalDate.now(), LocalTime.of(0, 0)).atZone(ZoneId.of("Europe/Madrid")).toInstant
    println(isPriceUpdated)
    println(fecha)
//    if (isPriceUpdated == true) {
//
//    } else {
//
//    }
  }

}

//def interpreter (results: List[TelegramResult], telegramClient: TelegramClient) = {
//  val tbd = results.flatMap(result => result.message.map(message =>(message.chat.id,message.text)))
//  tbd.traverse{
//  case (chatId,"/help") => telegramClient.sendMessage(chatId,"WIP")
//  case (chatId,message) => telegramClient.sendMessage(chatId,s"No se ha detectado ningún comando: $message")
//  }
//  }
