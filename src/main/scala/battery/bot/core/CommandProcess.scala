package battery.bot.core

import battery.bot.telegram.TelegramClient
import battery.bot.telegram.models.TelegramResult
import cats.implicits.toTraverseOps

class CommandProcess {

  def interpreter (results: List[TelegramResult], telegramClient: TelegramClient) = {
    val jsonResult = results.flatMap(result => result.message.map(message =>(message.chat.id,message.text.split(";").toList.map(_.trim).head)))
    jsonResult.traverse{
      case (chatId,"/help") => telegramClient.sendMessage(chatId,s"Entro en el comando")
      case (chatId,message) => telegramClient.sendMessage(chatId,s"No se ha detectado ningún comando: $message")
    }
  }
}