package battery.bot.core

import battery.bot.core.models._
import battery.bot.database.PersistenceService
import battery.bot.telegram.TelegramClient
import battery.bot.telegram.models.{TelegramChat, TelegramFrom, TelegramMessage, TelegramUpdate}
import cats.effect.IO
import cats.implicits.toTraverseOps

import java.time.Instant
import java.math.BigDecimal
import scala.jdk.CollectionConverters._

class CommandProcess(persistenceService: PersistenceService, telegramClient: TelegramClient) {

  def calculatePriceMessage(result: Update, minorPrice:BigDecimal): IO[Unit] = {
    for {
      time <- persistenceService.getBestTime(minorPrice, Instant.now())
      _    <- IO.pure(println(time))
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          s"El mejor momento para cargar el dispositivo es desde las $time"
        )
    } yield ()
  }

  def minPrices(chargingTime: Int, priceList: List[BigDecimal], result: Update, minorPrice: List[BigDecimal]): Unit = {
    val priceListFilter           = priceList.take(chargingTime)
    val priceListFilterSum        = priceListFilter.foldLeft(scala.BigDecimal(0))(_ + _)
    val priceListReduced          = priceList.tail
    val priceListReducedFilterSum = priceListReduced.take(chargingTime).foldLeft(scala.BigDecimal(0))(_ + _)
    //println(priceList)
    //println(priceListFilter.toString())
    //println(priceListReduced)
    //println(priceListReducedFilterSum.toString())
    println(priceListFilterSum.toString())
    println(priceListReducedFilterSum.toString())
    if (priceListFilterSum.compareTo(priceListReducedFilterSum) < 0) {
      val minorPrice = priceListFilter
      if (priceListReduced.length > chargingTime) {
        println("Entré en el segundo if bueno")
        minPrices(chargingTime, priceListReduced, result, minorPrice)
      } else if (priceListReduced.length == chargingTime.intValue) {
        calculatePriceMessage(result: Update, minorPrice.head)
      }
      else
          println("Entré en el segundo else")
          println(s"minor price vale $minorPrice")
      println(s" y quedan $priceListReduced valores")
         calculatePriceMessage(result: Update, minorPrice.head)
    } else {
      println("He entrado en el else")
      println(s"minor price vale $minorPrice")
      minPrices(chargingTime, priceListReduced, result, minorPrice)
    }
  }

  def calculateCommand(result: Update): IO[Unit] = {
    val data = result.message.text.split(";").toList
    data match {
      case _ :: deviceName :: Nil =>
        for {
          userid <- persistenceService.getUserID(result.message.from.username)
          //_ <- IO.pure(println(userid))
          chargingTime <- persistenceService.getDeviceChargingTime(userid, deviceName)
          //_ <- IO.pure(println(chargingTime))
          pricesList <- persistenceService.getPricesTime(Instant.now())
          //_ <- IO.pure(println(pricesList))
        } yield minPrices(chargingTime.toInt, pricesList, result, List())
      case _ =>
        telegramClient
          .sendMessage(
            result.message.chat.id,
            "El formato del comando no es el adecuado"
          )
          .void
    }
  }

  def startCommand(result: Update): IO[Unit] =
    for {
      _ <- persistenceService.addUser(result.message.from.username, 22, 6, false)
      _ <- telegramClient
        .sendMessage(
          result.message.chat.id,
          "Se ha configurado por defecto que se acuesta a las 22:00, se levanta a las 06:00 y que no desea cargar dispositivos durante la noche. Para saber como puede modificar sus ajustes escriba /help"
        )
    } yield ()

  def deviceRepeated(result: Update, deviceExist: Boolean, deviceName: String, chargingTime: Double): IO[Unit] = {
    if (deviceExist) {
      telegramClient
        .sendMessage(
          result.message.chat.id,
          "El usuario ya tiene un dispositivo con el mismo nombre, por favor introduzca un nombre diferente."
        )
        .void
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
      case _ :: deviceName :: chargingTime :: Nil =>
        for {
          userid        <- persistenceService.getUserID(result.message.from.username)
          userHasDevice <- persistenceService.existDeviceID(userid, deviceName)
          _             <- deviceRepeated(result, userHasDevice, deviceName, chargingTime.toDouble)
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
    val data = result.message.text.split(";").toList
    data match {
      case _ :: sleepingTime :: wakeUpTime :: nightCharge :: Nil
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
      case (update, message) if message.startsWith("/calcular") => calculateCommand(update)
      //case (update, message) if message.startsWith("/start")               => startCommand(update)
      //case (update, message) if message.startsWith("/addDevice")           => addDeviceCommand(update)
      //case (update, message) if message.startsWith("/verConfiguracion")    => checkSettings(update)
      //case (update, message) if message.startsWith("/editarConfiguracion") => updateSettings(update)
      //case (update, message) if message.startsWith("/help")                => telegramClient.sendMessage(update.message.chat.id, "WIP")
      case (update, message) =>
        telegramClient.sendMessage(update.message.chat.id, s"No se ha detectado ningún comando: $message")
    }
  }
}
