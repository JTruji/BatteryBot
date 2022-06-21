package battery.bot.database

import battery.bot.core.models.UserSettings
import battery.bot.database.DevicesQueries._
import battery.bot.database.UsersQueries._
import cats.effect.IO
import doobie.Transactor
import doobie.implicits._

import java.time.Instant
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

  def updateUserSettings(chatId:Long, sleepingTime: Int, wakeupTime: Int, nightCharge: Boolean): IO[Int] =
    updateSettings(chatId, sleepingTime, wakeupTime, nightCharge).run.transact(ta)

  def updateDeviceSettings(name: String, chargingTime: Double, userName: UUID): IO[Int] =
    updateChargingTime(name, chargingTime, userName).run.transact(ta)

  def getUserID(chatId:Long): IO[UUID] =
    getUserUUID(chatId).unique.transact(ta)

  def getUserSetting(chatId:Long): IO[UserSettings] =
    getSettings(chatId).unique.transact(ta)

  //def getUserDevicesName(chatId:Long): IO[List[String]] =
  //  userDevicesName(chatId).to[List].transact(ta)
}
