package battery.bot.core

import battery.bot.telegram.models.TelegramResult
import battery.bot.telegram.TelegramClient
import cats.effect.IO
import cats.implicits.toTraverseOps

class CommandProcess {

  def interpreter (results: List[TelegramResult], telegramClient: TelegramClient) = {
    val tbd = results.flatMap(result => result.message.map(message =>(message.chat.id,message.text)))
    tbd.traverse{
      case (chatId,"/help") => telegramClient.sendMessage(chatId,"WIP")
      case (chatId,message) => telegramClient.sendMessage(chatId,s"No se ha detectado ningún comando: $message")
    }
  }
}