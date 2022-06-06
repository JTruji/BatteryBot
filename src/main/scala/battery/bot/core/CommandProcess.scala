package battery.bot.core

import battery.bot.core.models.{Chat, From, Message, Update}
import battery.bot.database.PersistenceService
import battery.bot.telegram.TelegramClient
import battery.bot.telegram.models.{TelegramChat, TelegramFrom, TelegramMessage, TelegramUpdate}
import cats.effect.IO
import cats.implicits.toTraverseOps

class CommandProcess(persistenceService: PersistenceService, telegramClient: TelegramClient) {

  def startCommand(result:Update): IO[Unit] =
    for {
      userTelegram <- IO.pure(result.message.from.username)
      _            <- persistenceService.addUser(userTelegram, 22, 6, false)
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          "Se ha configurado por defecto que se acuesta a las 22:00, se levanta a las 06:00 y que no desea cargar dispositivos durante la noche. Para saber como puede modificar sus ajustes escriba /help"
        )
    } yield ()

  def interpreter(results: List[TelegramUpdate]): IO[List[AnyVal]] = {

    val updates = results.foldLeft(List.empty[Update]) {
      case (
            acc,
            TelegramUpdate(
              updateId,
              Some(
                TelegramMessage(
                  messageId,
                  Some(TelegramFrom(fromId, isBot, fromFirstName, Some(fromUsername), Some(languageCode))),
                  TelegramChat(chatId, Some(chatFirstName), Some(chatUsername), chatType),
                  text
                )
              )
            )
          ) =>
        acc :+ Update(
          updateId,
          Message(
            messageId,
            From(fromId, isBot, fromFirstName, fromUsername),
            Chat(chatId, chatFirstName, chatUsername, chatType),
            text
          )
        )
      case (acc, _) => acc
    }

    val telegramMessages =updates.map(up => (up, up.message.text))

    telegramMessages.traverse{
      case (update, message) if message.startsWith("/start")   => startCommand(update)
      case (update, "/help")  => telegramClient.sendMessage(update.message.chat.id, "WIP")
      case (update, message)  => telegramClient.sendMessage(update.message.chat.id, s"No se ha detectado ningún comando: $message")
    }
  }
}
