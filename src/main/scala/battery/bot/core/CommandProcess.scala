package battery.bot.core

import battery.bot.core.models.{Chat, From, Message, Update}
import battery.bot.database.PersistenceService
import battery.bot.telegram.TelegramClient
import battery.bot.telegram.models.{TelegramChat, TelegramFrom, TelegramMessage, TelegramUpdate}
import cats.effect.IO
import cats.implicits.toTraverseOps

import java.util.UUID

class CommandProcess(persistenceService: PersistenceService, telegramClient: TelegramClient) {

  //  START COMMAND //
  def startCommand(result: Update): IO[Unit] = {
    for {
      _ <- persistenceService.addUser(result.message.chat.id, 22, 6, true)
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          "Se ha configurado por defecto que se acuesta a las 22:00, se levanta a las 06:00 y que desea cargar dispositivos durante la noche. Para conocer qué más puedo hacer escriba /help"
        )
    } yield ()
  }

  //  CHECK SETTING COMMAND //
  def sendSettings(sleepingTime: Int, wakeUpTime: Int, nightCharge: Boolean, result: Update): IO[Unit] = {
    if (nightCharge) {
      telegramClient
        .sendMessage(
          result.message.chat.id,
          s"Su configuración es la siguiente: se levanta a las $wakeUpTime, se acuesta a las $sleepingTime y permite cargar durante la noche"
        )
        .void
    } else {
      telegramClient
        .sendMessage(
          result.message.chat.id,
          s"Su configuración es la siguiente: se levanta a las $wakeUpTime, se acuesta a las $sleepingTime y no permite cargar durante la noche"
        )
        .void
    }
  }

  def checkSettingsCommand(result: Update): IO[Unit] = {
    for {
      userUUID     <- persistenceService.getUserUUID(result.message.chat.id)
      settingsList <- persistenceService.getUserSetting(userUUID)
      _            <- sendSettings(settingsList.sleepingTime, settingsList.wakeUpTime, settingsList.nightCharge, result)
    } yield ()
  }

  // EDIT SETTING COMMAND //
  def updateSettingsCommand(result: Update): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case sleepingTime :: wakeUpTime :: nightCharge :: Nil
          if sleepingTime.toIntOption.nonEmpty && wakeUpTime.toIntOption.nonEmpty && nightCharge.toBooleanOption.nonEmpty =>
        for {
          userUUID <- persistenceService.getUserUUID(result.message.chat.id)
          _ <- persistenceService
            .updateUserSettings(
              userUUID,
              sleepingTime.toInt,
              wakeUpTime.toInt,
              nightCharge.toBoolean
            )
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
              "El formato del comando no es el adecuado, por favor siga el siguiente => /editarConfiguracion;[Número];[Número];[true o false]"
            )
        } yield ()
    }
  }

  // ADD NEW DEVICE COMMAND //
  def addDeviceCommand(result: Update, userUUID: UUID, devicesList: List[String]): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case deviceName :: _ :: Nil if devicesList.contains(deviceName) =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El usuario ya tiene un dispositivo con ese nombre, cambielo"
          )
          .void
      case deviceName :: chargingTime :: Nil
          if chargingTime.toDoubleOption.nonEmpty && !devicesList.contains(deviceName) =>
        for {
          _ <- persistenceService.addDevice(userUUID, deviceName, chargingTime.toDouble)
          _ <- telegramClient
            .sendMessage(
              result.message.chat.id,
              "El dispositivo se ha guardado correctamente."
            )
        } yield ()
      case _ =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El formato del comando no es el adecuado, por favor siga el siguiente => /nuevoDispositivo;[Nombre del dispositivo];[Tiempo carga]"
          )
          .void
    }
  }

  def newDeviceCommand(result: Update): IO[Unit] = {
    for {
      userUUID    <- persistenceService.getUserUUID(result.message.chat.id)
      devicesList <- persistenceService.getUserDevicesName(userUUID)
      _           <- addDeviceCommand(result, userUUID, devicesList)
    } yield ()
  }

  // EDIT DEVICE COMMAND //
  def updateDevice(result: Update, userUUID: UUID, devicesList: List[String]): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case deviceName :: chargingTime :: Nil
          if chargingTime.toDoubleOption.nonEmpty && deviceName.nonEmpty && devicesList.contains(deviceName) =>
        for {
          _ <- persistenceService
            .updateDeviceSettings(
              deviceName,
              chargingTime.toDouble,
              userUUID
            )
          _ <- telegramClient
            .sendMessage(
              result.message.chat.id,
              "El dispositivo ha sido actualizado"
            )
        } yield ()
      case deviceName :: chargingTime :: Nil if !devicesList.contains(deviceName) && devicesList.nonEmpty =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            s"El usuario no tiene ningún dispositivo con el nombre $deviceName"
          )
          .void
      case _ :: _ :: Nil if devicesList.isEmpty =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El usuario no tiene ningún dispositivo, emplee el comando /nuevoDispositivo;[Nombre del dispositivo];[Tiempo carga] para añadir uno"
          )
          .void
      case _ =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El formato del comando no es el adecuado, por favor siga el siguiente => /editarDispositivo;[Nombre];[Tiempo de carga]"
          )
          .void
    }
  }

  def editDeviceCommand(result: Update): IO[Unit] = {
    for {
      userUUID    <- persistenceService.getUserUUID(result.message.chat.id)
      devicesList <- persistenceService.getUserDevicesName(userUUID)
      _           <- updateDevice(result, userUUID, devicesList)
    } yield (userUUID, devicesList)
  }

  // DELETE DEVICE COMMAND //
  def deleteDevice(result: Update, userUUID: UUID, devicesList: List[String]): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case deviceName :: Nil if !devicesList.contains(deviceName) =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            s"El usuario no tiene un dispositivo con el nombre $deviceName"
          )
          .void
      case deviceName :: Nil if devicesList.contains(deviceName) =>
        for {
          _ <- persistenceService.removeDevice(userUUID, deviceName)
          _ <- telegramClient
            .sendMessage(
              result.message.chat.id,
              "El dispositivo se ha borrado correctamente."
            )
        } yield ()
      case _ =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El formato del comando no es el adecuado"
          )
          .void
    }
  }

  def deleteDeviceCommand(result: Update): IO[Unit] = {
    for {
      userUUID    <- persistenceService.getUserUUID(result.message.chat.id)
      devicesList <- persistenceService.getUserDevicesName(userUUID)
      _           <- deleteDevice(result, userUUID, devicesList)
    } yield ()
  }

  // CALCULAR COMMAND //

  // INTERPRETER //
  def interpreter(result: TelegramUpdate): IO[Unit] = {
    result match {
      case TelegramUpdate(
            updateId,
            Some(
              TelegramMessage(
                messageId,
                Some(TelegramFrom(fromId, isBot, fromFirstName, _, _)),
                TelegramChat(chatId, Some(chatFirstName), _, chatType),
                text
              )
            )
          ) =>
        val update = Update(
          updateId,
          Message(
            messageId,
            From(fromId, isBot, fromFirstName),
            Chat(chatId, chatFirstName, chatType),
            text
          )
        )

        val telegramMessage = (update, update.message.text)

        telegramMessage match {
          case (update, message) if message.toLowerCase.startsWith("/start")            => startCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/verconfiguracion") => checkSettingsCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/editarconfiguracion") =>
            updateSettingsCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/nuevodispositivo")  => newDeviceCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/editardispositivo") => editDeviceCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/borrardispositivo") => deleteDeviceCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/help") =>
            telegramClient.sendMessage(
              update.message.chat.id,
              "Esto bot le ayudará a ahorrar dinero buscando las horas más baratas para cargar dispositivos eléctricos para usar este bot dispone de los siguientes comandos:" +
                "\n/nuevoDispositivo ???" +
                "\n/editarDispositivo ???" +
                "\n/borrarDispositivo ???" +
                "\n/verConfiguracion ???" +
                "\n/editarConfiguracion ???" +
                "\n/calcular ???"
            )
          case (update, message) =>
            telegramClient.sendMessage(update.message.chat.id, s"No se ha detectado ningún comando")
        }
      case _ => IO.unit
    }

  }
}
