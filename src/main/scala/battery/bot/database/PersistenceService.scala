package battery.bot.database

import battery.bot.core.models.UserSettings
import battery.bot.database.DevicesQueries._
import battery.bot.database.UsersQueries._
import battery.bot.database.PricesQueries._
import cats.effect.IO
import doobie.Transactor
import doobie.implicits._

import java.time.{Instant, LocalDateTime}
import java.util.UUID

class PersistenceService(ta: Transactor[IO]) {

  def addScraperPrices(pricesList: List[(Instant, BigDecimal)]): IO[Int] =
    PricesQueries.insertManyPrices.updateMany(pricesList).transact(ta)

  def addUser(name: String, sleepingTime: Int, wakeUpTime: Int, nightCharge: Boolean): IO[Int] =
    insertUsers(UUID.randomUUID(), name, sleepingTime, wakeUpTime, nightCharge).run.transact(ta)

  def addDevice(userId: UUID, name: String, chargingTime: Double): IO[Int] =
    insertDevices(UUID.randomUUID(), userId, name, chargingTime).run.transact(ta)

  def removeDevice(userId: UUID, deviceName: String): IO[Int] =
    deleteDevice(userId, deviceName).run.transact(ta)

  def updateUserSettings(name: String, sleepingTime: Int, wakeupTime: Int, nightCharge: Boolean): IO[Int] =
    updateSettings(name, sleepingTime, wakeupTime, nightCharge).run.transact(ta)

  def updateDeviceSettings(name: String, chargingTime: Double, userName: UUID): IO[Int] =
    updateChargingTime(name, chargingTime, userName).run.transact(ta)

  def getUserID(username: String): IO[UUID] =
    getUserUUID(username).unique.transact(ta)

  def getUserSetting(userName: String): IO[UserSettings] =
    getSettings(userName).unique.transact(ta)

  def getDeviceChargingTime(userName: UUID, deviceName: String): IO[Double] =
    getChargingTime(userName, deviceName).unique.transact(ta)

  def getPricesTime(time: Instant): IO[List[BigDecimal]] =
    pricesTime(time).to[List].transact(ta)

  def getBestTime(price:BigDecimal, time: Instant): IO[LocalDateTime] =
    getTime(price, time).unique.transact(ta)

  def getUserDevicesName(userName: UUID): IO[List[String]] =
    userDevicesName(userName).to[List].transact(ta)
}
