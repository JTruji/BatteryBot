package battery.bot.core

import battery.bot.core.models.{Chat, From, Message, Update}
import battery.bot.database.PersistenceService
import battery.bot.telegram.TelegramClient
import battery.bot.telegram.models.{TelegramChat, TelegramFrom, TelegramMessage, TelegramUpdate}
import cats.effect.IO
import cats.implicits.toTraverseOps

import java.time.Instant
import java.util.UUID

class CommandProcess(persistenceService: PersistenceService, telegramClient: TelegramClient) {

  //  START COMMAND //
  def startCommandMessage(result: Update): IO[Unit] = {
    for {
      _ <- persistenceService.addUser(result.message.chat.id, 22, 6, true)
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          "Se ha configurado por defecto que se acuesta a las 22:00, se levanta a las 06:00 y que desea cargar dispositivos durante la noche. Para conocer qué más puedo hacer escriba /help"
        )
    } yield ()
  }

  def startCommand(result: Update) = {
    val userUUID = persistenceService.getUserUUID(result.message.chat.id)
    if (userUUID == null) {
      startCommandMessage(result: Update)
    } else {
      telegramClient
        .sendMessage(
          result.message.chat.id,
          s"Ya posee una cuenta"
        )
        .void
    }
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
        if (sleepingTime.toInt >= 0 && sleepingTime.toInt <= 23 && wakeUpTime.toInt >= 0 && wakeUpTime.toInt <= 23) {
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
        } else {
          telegramClient
            .sendMessage(
              result.message.chat.id,
              "Introduzca unas horas válidas"
            )
            .void
        }
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
        if (chargingTime.toDouble > 12.0) {
          telegramClient
            .sendMessage(
              result.message.chat.id,
              "Este bot no admite dispositivos que tarden tanto tiempo en completarse."
            )
            .void
        } else if (chargingTime.toDouble < 0) {
          telegramClient
            .sendMessage(
              result.message.chat.id,
              "No te admito ese dispositivo porque en este bot se siguen las leyes de la termodinámica."
            )
            .void
        } else {
          for {
            _ <- persistenceService.addDevice(userUUID, deviceName, chargingTime.toDouble)
            _ <- telegramClient
              .sendMessage(
                result.message.chat.id,
                "El dispositivo se ha guardado correctamente."
              )
          } yield ()
        }
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
        if (chargingTime.toDouble > 12.0) {
          telegramClient
            .sendMessage(
              result.message.chat.id,
              "Este bot no admite dispositivos que tarden tanto tiempo en completarse."
            )
            .void
        } else if (chargingTime.toDouble <= 0) {
          telegramClient
            .sendMessage(
              result.message.chat.id,
              "No te admito ese dispositivo porque en este bot se siguen las leyes de la termodinámica."
            )
            .void
        } else {
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
        }
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
            "El usuario no tiene ningún dispositivo, emplee el comando /nuevoDispositivo;[Nombre del dispositivo];[Tiempo de carga] para añadir uno"
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

  // CHECK DEVICES COMMAND
  def checkDevicesCommandMessage(result: Update, devicesList: List[String]): IO[Unit] = {
    if (devicesList.isEmpty) {
      telegramClient
        .sendMessage(
          result.message.chat.id,
          s"No tiene guardado ningún dispositivo, use el comando /help para aprender a añadir uno"
        )
        .void
    } else {
      telegramClient
        .sendMessage(
          result.message.chat.id,
          s"Dispone de los siguientes dispositivos: \n ${devicesList.mkString("\n")}"
        )
        .void
    }
  }

  def checkDevicesCommand(result: Update): IO[Unit] = {
    for {
      userUUID    <- persistenceService.getUserUUID(result.message.chat.id)
      devicesList <- persistenceService.getUserDevicesName(userUUID)
      _           <- checkDevicesCommandMessage(result, devicesList)
    } yield ()
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
            "El formato del comando no es el adecuado, por favor siga el siguiente => /borrarDispositivo;[Nombre]"
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

  // CALCULATE COMMAND //
  def notEnoughTime(hour: Int, totalHour: Int, result: Update): IO[Unit] = {
    val chargePercentage = 100 * hour / totalHour
    telegramClient
      .sendMessage(
        result.message.chat.id,
        s"No quedan suficientes horas para cargar por completo el dispositivo, si lo conecta ahora a las 0:00 estará aproximadamente al $chargePercentage%"
      )
  }

  def calculateCommandMessage(result: Update, firstPrice: BigDecimal): IO[Unit] = {
    for {
      time <- persistenceService.getLowerPriceTime(firstPrice, Instant.now())
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          s"El mejor momento para cargar el dispositivo es desde las ${time.getHour} con un precio de $firstPrice"
        )
    } yield ()
  }

  def calculateCommand(
      chargingTime: Double,
      priceList: List[BigDecimal],
      result: Update,
      minorPrice: List[BigDecimal]
  ): IO[Unit] = {
    val priceListFilter       = priceList.take(chargingTime.intValue)
    val priceListFilterSum    = priceListFilter.sum
    val newPriceList          = priceList.tail
    val newPriceListFilter    = newPriceList.take(chargingTime.intValue)
    val newPriceListFilterSum = newPriceListFilter.sum
    println("\n")
    println(priceListFilter)
    println(priceListFilterSum)
    println(newPriceList)
    println(newPriceListFilter)
    println(newPriceListFilterSum)
    if (
      (priceListFilterSum < newPriceListFilterSum) & (newPriceList.length > chargingTime.intValue) & (priceListFilterSum < minorPrice.sum)
    ) {
      println("He entrado en el primero")
      calculateCommand(chargingTime, newPriceList, result, priceListFilter)
    } else if ((newPriceList.length == chargingTime.intValue) & (minorPrice.sum < newPriceListFilterSum)) {
      println("He entrado en el segundo")
      calculateCommandMessage(result, minorPrice.head)
    } else if ((newPriceList.length == chargingTime.intValue) & (minorPrice.sum > newPriceListFilterSum)) {
      // Todavia no ha entrado aquí
      println("He entrado en el tercero")
      calculateCommandMessage(result, newPriceListFilter.head)
    } else if (newPriceList.length < chargingTime.intValue) {
      notEnoughTime(newPriceList.length, chargingTime.intValue, result)
    } else {
      println("He entrado en el cuarto")
      calculateCommand(chargingTime, newPriceList, result, minorPrice)
    }
  }

  def calculateCommandFormat(
      userUUID: UUID,
      devicesList: List[String],
      sleepingTime: Int,
      wakeUpTime: Int,
      nightCharge: Boolean,
      result: Update
  ): IO[Unit] = {
    val data = result.message.text.split(";").toList.tail
    data match {
      case deviceName :: Nil if deviceName.nonEmpty && devicesList.contains(deviceName) && nightCharge =>
        for {
          chargingTime <- persistenceService.getDeviceChargingTime(userUUID, deviceName)
          pricesList   <- persistenceService.getPricesTimeTrue(Instant.now())
          _ = println(pricesList)
          _ <- calculateCommand(chargingTime.toDouble, pricesList, result, List(24))
        } yield ()
      case deviceName :: Nil if deviceName.nonEmpty && devicesList.contains(deviceName) && !nightCharge =>
        for {
          chargingTime    <- persistenceService.getDeviceChargingTime(userUUID, deviceName)
          pricesListFalse <- persistenceService.getPricesTimeFalse(Instant.now(), wakeUpTime, sleepingTime)
          _ = println(pricesListFalse)
          _ = println("He entrado en el false")
          _ <- calculateCommand(chargingTime.toDouble, pricesListFalse, result, List(24))
        } yield ()
      case deviceName :: Nil if !devicesList.contains(deviceName) && devicesList.nonEmpty =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            s"El usuario no tiene ningún dispositivo con el nombre $deviceName"
          )
          .void
      case _ :: Nil if devicesList.isEmpty =>
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
            "El formato del comando no es el adecuado, por favor siga el siguiente => /calcular;[Nombre del dispositivo]"
          )
          .void
    }
  }

  def calculateCommand(result: Update): IO[Unit] = {
    for {
      userUUID     <- persistenceService.getUserUUID(result.message.chat.id)
      devicesList  <- persistenceService.getUserDevicesName(userUUID)
      settingsList <- persistenceService.getUserSetting(userUUID)
      _ <- calculateCommandFormat(
        userUUID,
        devicesList,
        settingsList.sleepingTime,
        settingsList.wakeUpTime,
        settingsList.nightCharge,
        result
      )
    } yield ()
  }

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
          case (update, message) if message.toLowerCase.startsWith("/verdispositivos")   => checkDevicesCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/borrardispositivo") => deleteDeviceCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/calcular")          => calculateCommand(update)
          case (update, message) if message.toLowerCase.startsWith("/help") =>
            telegramClient.sendMessage(
              update.message.chat.id,
              "Esto bot le ayudará a ahorrar dinero buscando las horas más baratas para cargar dispositivos eléctricos para usar este bot dispone de los siguientes comandos:" +
                "\n/nuevoDispositivo le permitirá añadir un nuevo dispositivo a su usuario, el formato del comando es el siguiente: /nuevoDispositivo;[Nombre del dispositivo];[Tiempo de carga]" +
                "\n/editarDispositivo le permitirá modificar las características de uno de sus dispositivos, el formato del comando es el siguiente: /editarDispositivo;[Nombre del dispositivo];[Tiempo de carga]" +
                "\n/verDispositivos le permitirá ver todos sus dispositivos, el formato del comando es el siguiente: /verDispositivos" +
                "\n/borrarDispositivo le permitirá borrar uno de sus dispositivos, el formato del comando es el siguiente: /borrarDispositivo:[Nombre del dispositivo]" +
                "\n/verConfiguracion le permitirá ver la configuración de su usuario, el formato es el siguiente: /verConfiguracion" +
                "\n/editarConfiguracion le permitirá editar la configuración de su usuario, el formato es el siguiente: /editarConfiguracion;[Hora a la que se acuesta],[Hora a la que se levanta],[true o false, dependiendo si desea cargar durante la noche]" +
                "\n/calcular le informará de la hora a la quer debe conectar el dispositivo para ahorra el máximo posible en su factura de la luz, el formato es el siguiente: /calcular;[Nombre del dispositivo]"
            )
          case (update, message) =>
            telegramClient.sendMessage(update.message.chat.id, s"No se ha detectado ningún comando")
        }
      case _ => IO.unit
    }

  }
}
