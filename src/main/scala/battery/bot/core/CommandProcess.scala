package battery.bot.core

import battery.bot.core.models.{Chat, From, Message, Update}
import battery.bot.database.PersistenceService
import battery.bot.telegram.TelegramClient
import battery.bot.telegram.models.{TelegramChat, TelegramFrom, TelegramMessage, TelegramUpdate}
import cats.effect.IO
import cats.implicits.toTraverseOps
import pureconfig.ConfigReader.Result

class CommandProcess(persistenceService: PersistenceService, telegramClient: TelegramClient) {

  def startCommand(result: Update): IO[Unit] =
    for {
      _ <- persistenceService.addUser(result.message.from.username, 22, 6, false)
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          "Se ha configurado por defecto que se acuesta a las 22:00, se levanta a las 06:00 y que no desea cargar dispositivos durante la noche. Para saber como puede modificar sus ajustes escriba /help"
        )
    } yield ()

  def deviceRepited(result: Update, deviceExist: Boolean, deviceName: String, chargingTime: Double): IO[Unit] = {
    if (deviceExist) {
      for {
        _ <- telegramClient
          .sendMessage(
            result.message.chat.id,
            "El usuario ya tiene un dispositivo con el mismo nombre, por favor introduzca un nombre diferente."
          )
      } yield ()
    } else {
      for {
        userID <- persistenceService.getUserID(result.message.from.username)
        _      <- persistenceService.addDevice(userID, deviceName, chargingTime)
        _ <- telegramClient
          .sendMessage(
            result.message.chat.id,
            "El dispositivo se ha guardado correctamente."
          )
      } yield ()
    }
  }

  def addDeviceCommand(result: Update): IO[Unit] = {
    val data = result.message.text.split(";").toList
    data match {
      case comand :: deviceName :: chargingTime :: Nil =>
        for {
          userid        <- persistenceService.getUserID(result.message.from.username)
          userHasDevice <- persistenceService.getDeviceID(userid, deviceName)
          _             <- deviceRepited(result, userHasDevice, deviceName, chargingTime.toDouble)
        } yield ()
      case _ =>
        for {
          _ <- telegramClient
            .sendMessage(
              result.message.chat.id,
              "El formato del comando no es el adecuado"
            )
        } yield ()
    }
  }

  def sendSettings(sleepingTime: String, wakeUpTime: String, nightCharge: Boolean, result: Update): IO[Unit] = {
    if (nightCharge) {
      for {
        _ <- telegramClient
          .sendMessage(
            result.message.chat.id,
            s"El usuario se levanta a las $sleepingTime, se acuesta a las $wakeUpTime y permite cargar durante la noche"
          )
      } yield ()
    } else {
      for {
        _ <- telegramClient
          .sendMessage(
            result.message.chat.id,
            s"El usuario se levanta a las $sleepingTime, se acuesta a las $wakeUpTime y no permite cargar durante la noche"
          )
      } yield ()
    }
  }

  def checkSettings(result: Update): IO[Unit] = {
    for {
      settingsList <- persistenceService.getUserSetting(result.message.from.username)
      _            <- sendSettings(settingsList._1, settingsList._2, settingsList._3, result)
    } yield ()
  }

  def updateSettings(result: Update): IO[Unit] = {
    val data = result.message.text.split(";").toList
    data match {
      case comand :: sleepingTime :: wakeUpTime :: nightCharge :: Nil =>
        for {
          _      <- persistenceService.updateUserSleepingTime(result.message.from.username, sleepingTime.toInt)
          _      <- persistenceService.updateUserwakeupTime(result.message.from.username, wakeUpTime.toInt)
          _      <- persistenceService.updateUsernightCharge(result.message.from.username, nightCharge.toBoolean)
          _ <- telegramClient
            .sendMessage(
              result.message.chat.id,
              "El usuario ha sido actualizado"
            )
        } yield ()
      case _ =>
        for {
          _ <- telegramClient
            .sendMessage(
              result.message.chat.id,
              "El formato del comando no es el adecuado"
            )
        } yield ()
    }
  }

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

    val telegramMessages = updates.map(up => (up, up.message.text))

    telegramMessages.traverse {
      case (update, message) if message.startsWith("/start")               => startCommand(update)
      case (update, message) if message.startsWith("/addDevice")           => addDeviceCommand(update)
      case (update, message) if message.startsWith("/verConfiguracion")    => checkSettings(update)
      case (update, message) if message.startsWith("/editarConfiguracion") => updateSettings(update)
      case (update, message) if message.startsWith("/help")                => telegramClient.sendMessage(update.message.chat.id, "WIP")
      case (update, message) =>
        telegramClient.sendMessage(update.message.chat.id, s"No se ha detectado ningún comando: $message")
    }
  }
}
