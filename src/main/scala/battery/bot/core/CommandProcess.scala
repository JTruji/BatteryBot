package battery.bot.core

import battery.bot.core.models._
import battery.bot.database.PersistenceService
import battery.bot.telegram.TelegramClient
import battery.bot.telegram.models.{TelegramChat, TelegramFrom, TelegramMessage, TelegramUpdate}
import cats.effect.IO
import cats.implicits.toTraverseOps
import java.time.Instant

class CommandProcess(persistenceService: PersistenceService, telegramClient: TelegramClient) {

  def calculatePriceMessage(result: Update, minorPrice: BigDecimal): IO[Unit] = {
    for {
      time <- persistenceService.getBestTime(minorPrice, Instant.now())
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          s"El mejor momento para cargar el dispositivo es desde las ${time.getHour}"
        )
    } yield ()
  }

  def minPrices(
      chargingTime: Int,
      priceList: List[BigDecimal],
      result: Update,
      minorPrice: List[BigDecimal]
  ): IO[Unit] = {
    val priceListFilter       = priceList.take(chargingTime)
    val priceListFilterSum    = priceListFilter.sum
    val newPriceList          = priceList.tail
    val newPriceListFilter    = newPriceList.take(chargingTime)
    val newPriceListFilterSum = newPriceListFilter.sum
    if ((priceListFilterSum < newPriceListFilterSum) & (newPriceList.length > chargingTime.toInt)) {
      minPrices(chargingTime, newPriceList, result, priceListFilter)
    } else if ((newPriceList.length == chargingTime.intValue) & (minorPrice.sum < newPriceListFilterSum)) {
      calculatePriceMessage(result, minorPrice.head)
    //} else if ((newPriceList.length == chargingTime.intValue) & (minorPrice.sum > newPriceListFilterSum)) {
    //  calculatePriceMessage(result, newPriceListFilter.head)
    } else if ((priceListFilter.length > newPriceListFilter.length) & minorPrice.isEmpty){
      telegramClient
        .sendMessage(
          result.message.chat.id,
          "Me faltan horas bro"
        ).void
    } else {
      minPrices(chargingTime, newPriceList, result, minorPrice)
    }
  }

  def calculateCommand(result: Update): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case deviceName :: Nil =>
        for {
          userid       <- persistenceService.getUserID(result.message.from.username)
          chargingTime <- persistenceService.getDeviceChargingTime(userid, deviceName)
          pricesList   <- persistenceService.getPricesTime(Instant.now())
          _            <- minPrices(chargingTime.toInt, pricesList, result, List())
        } yield ()
      case _ =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El formato del comando no es el adecuado, siga el siguiente /calcular;[Nombre del dispositivo]"
          )
          .void
    }
  }

  def userDevicesForDelete(result: Update): IO[Unit] = {
    for {
      userId      <- persistenceService.getUserID(result.message.from.username)
      devicesList <- persistenceService.getUserDevicesName(userId)
      _           <- deleteDevice(result, devicesList)
    } yield ()
  }

  def deleteDevice(result: Update, devicesList: List[String]): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case deviceName :: Nil if !devicesList.contains(deviceName) =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El usuario no tiene un dispositivo con ese nombre"
          )
          .void
      case deviceName :: Nil if devicesList.contains(deviceName) =>
        for {
          userID <- persistenceService.getUserID(result.message.from.username)
          _      <- persistenceService.removeDevice(userID, deviceName)
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

  def startCommand(result: Update): IO[Unit] = {
    for {
      _ <- persistenceService.addUser(result.message.from.username, 22, 6, false)
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          "Se ha configurado por defecto que se acuesta a las 22:00, se levanta a las 06:00 y que no desea cargar dispositivos durante la noche. Para saber como puede modificar sus ajustes escriba /help"
        )
    } yield ()
  }

  def addDeviceCommand(result: Update, devicesList: List[String]): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case deviceName :: chargingTime :: Nil if devicesList.contains(deviceName) =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El usuario ya tiene un dispositivo con ese nombre, cambielo"
          )
          .void
      case deviceName :: chargingTime :: Nil if !devicesList.contains(deviceName) =>
        for {
          userID <- persistenceService.getUserID(result.message.from.username)
          _      <- persistenceService.addDevice(userID, deviceName, chargingTime.toDouble)
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
            "El formato del comando no es el adecuado"
          )
          .void
    }
  }

  def sendSettings(sleepingTime: Int, wakeUpTime: Int, nightCharge: Boolean, result: Update): IO[Unit] = {
    if (nightCharge) {
      telegramClient
        .sendMessage(
          result.message.chat.id,
          s"El usuario se levanta a las $sleepingTime, se acuesta a las $wakeUpTime y permite cargar durante la noche"
        )
        .void
    } else {
      telegramClient
        .sendMessage(
          result.message.chat.id,
          s"El usuario se levanta a las $sleepingTime, se acuesta a las $wakeUpTime y no permite cargar durante la noche"
        )
        .void
    }
  }

  def checkSettings(result: Update): IO[Unit] = {
    for {
      settingsList <- persistenceService.getUserSetting(result.message.from.username)
      _            <- sendSettings(settingsList.sleepingTime, settingsList.wakeUpTime, settingsList.nightCharge, result)
    } yield ()
  }

  def updateSettings(result: Update): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case sleepingTime :: wakeUpTime :: nightCharge :: Nil
          if sleepingTime.toIntOption.nonEmpty && wakeUpTime.toIntOption.nonEmpty && nightCharge.toBooleanOption.nonEmpty =>
        for {
          _ <- persistenceService
            .updateUserSettings(
              result.message.from.username,
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

  def userDevices(result: Update): IO[Unit] = {
    for {
      userId      <- persistenceService.getUserID(result.message.from.username)
      devicesList <- persistenceService.getUserDevicesName(userId)
      _           <- updateDevice(result, devicesList)
    } yield ()
  }

  def userDevicesForAdd(result: Update): IO[Unit] = {
    for {
      userId      <- persistenceService.getUserID(result.message.from.username)
      devicesList <- persistenceService.getUserDevicesName(userId)
      _           <- addDeviceCommand(result, devicesList)
    } yield ()
  }

  def updateDevice(result: Update, devicesList: List[String]): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case name :: chargingTime :: Nil if chargingTime.toDoubleOption.nonEmpty && devicesList.contains(name) =>
        for {
          userId <- persistenceService.getUserID(result.message.from.username)
          _ <- persistenceService
            .updateDeviceSettings(
              name,
              chargingTime.toDouble,
              userId
            )
          _ <- telegramClient
            .sendMessage(
              result.message.chat.id,
              "El dispositivo ha sido actualizado"
            )
        } yield ()
      case name :: chargingTime :: Nil if !devicesList.contains(name) && devicesList.nonEmpty =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            s"El usuario no tiene ningún dispositivo con el nombre $name"
          )
          .void
      case _ :: _ :: Nil if devicesList.isEmpty =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            s"El usuario no tiene ningún dispositivo"
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

  def interpreter(results: List[TelegramUpdate]): IO[List[AnyVal]] = {
    val updates = results.foldLeft(List.empty[Update]) {
      case (
            acc,
            TelegramUpdate(
              updateId,
              Some(
                TelegramMessage(
                  messageId,
                  Some(TelegramFrom(fromId, isBot, fromFirstName, Some(fromUsername), Some(_))),
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
      case (update, message) if message.startsWith("/calcular")            => calculateCommand(update)
      case (update, message) if message.startsWith("/start")               => startCommand(update)
      case (update, message) if message.startsWith("/addDevice")           => userDevicesForAdd(update)
      case (update, message) if message.startsWith("/borrarDispositivo")   => userDevicesForDelete(update)
      case (update, message) if message.startsWith("/verConfiguracion")    => checkSettings(update)
      case (update, message) if message.startsWith("/editarConfiguracion") => updateSettings(update)
      case (update, message) if message.startsWith("/editarDispositivo")   => userDevices(update)
      case (update, message) if message.startsWith("/help")                => telegramClient.sendMessage(update.message.chat.id, "WIP")
      case (update, message) =>
        telegramClient.sendMessage(update.message.chat.id, s"No se ha detectado ningún comando: $message")
    }
  }
}
