package battery.bot.database

import battery.bot.core.models.UserSettings
import battery.bot.database.DevicesQueries._
import battery.bot.database.PricesQueries._
import battery.bot.database.UsersQueries._
import battery.bot.database.UpdateIDQueries._
import cats.effect.IO
import doobie.Transactor
import doobie.implicits._

import java.time.{Instant, LocalDate, LocalDateTime, LocalTime, ZoneId}
import java.util.UUID

class PersistenceService(ta: Transactor[IO]) {

  def addScraperPrices(pricesList: List[(Instant, BigDecimal)]): IO[Int] =
    PricesQueries.insertManyPrices.updateMany(pricesList).transact(ta)

  def addUser(chatId: Long, sleepingTime: Int, wakeUpTime: Int, nightCharge: Boolean): IO[Int] =
    insertUsers(UUID.randomUUID(), chatId, sleepingTime, wakeUpTime, nightCharge).run.transact(ta)

  def addDevice(userId: UUID, name: String, chargingTime: Double): IO[Int] =
    insertDevices(UUID.randomUUID(), userId, name, chargingTime).run.transact(ta)

  def removeDevice(userId: UUID, deviceName: String): IO[Int] =
    deleteDevice(userId, deviceName).run.transact(ta)

  def updateUserSettings(userUUID: UUID, sleepingTime: Int, wakeupTime: Int, nightCharge: Boolean): IO[Int] =
    updateSettings(userUUID, sleepingTime, wakeupTime, nightCharge).run.transact(ta)

  def updateDeviceSettings(name: String, chargingTime: Double, userUUID: UUID): IO[Int] =
    updateChargingTime(name, chargingTime, userUUID).run.transact(ta)

  def getUserUUID(chatId: Long): IO[Option[UUID]] =
    getUserId(chatId).unique.transact(ta)

  def getUserSetting(userUUID: UUID): IO[UserSettings] =
    getSettings(userUUID).unique.transact(ta)

  def getUserDevicesName(userUUID: UUID): IO[List[String]] =
    userDevicesName(userUUID).to[List].transact(ta)

  def getDeviceChargingTime(userUUID: UUID, deviceName: String): IO[Double] =
    deviceChargingTime(userUUID, deviceName).unique.transact(ta)

  def getLowerPriceTime(price: BigDecimal, time: Instant): IO[LocalDateTime] =
    lowerPriceTime(price, time).unique.transact(ta)

  def getPricesTimeTrue(time: Instant): IO[List[BigDecimal]] =
    pricesTimeTrue(time).to[List].transact(ta)

  def getPricesTimeFalse(time: Instant, wakeUpTime: Int, sleepingTime: Int): IO[List[BigDecimal]] =
    pricesTimeFalse(
      time,
      LocalDateTime.of(LocalDate.now(), LocalTime.of(wakeUpTime - 1, 0)).atZone(ZoneId.of("Europe/Madrid")).toInstant,
      LocalDateTime.of(LocalDate.now(), LocalTime.of(sleepingTime - 1, 0)).atZone(ZoneId.of("Europe/Madrid")).toInstant
    ).to[List].transact(ta)

  def updateTelegramUpdate(updateID: Long): IO[Int] =
    updateUpdateID(updateID, Instant.now()).run.transact(ta)

  def getLastUpdateId =
    getUpdateID.unique.transact(ta)
}
