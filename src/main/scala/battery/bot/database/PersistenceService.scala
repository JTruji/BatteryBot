package battery.bot.database

import battery.bot.database.DevicesQueries.{getDeviceUUID, insertDevices}
import battery.bot.database.UsersQueries.{getSettings, getUserUUID, insertUsers, updateNightCharge, updateSleepingTime, updateWakeUpTime}
import cats.effect.IO
import doobie.Transactor
import doobie.implicits._

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PersistenceService(ta: Transactor[IO]) {

  def addScraperPrices(pricesList: List[(Instant, BigDecimal)]): IO[Int] =
    PricesQueries.insertManyPrices.updateMany(pricesList).transact(ta)

  def addUser(name: String, sleepingTime: Int, wakeUpTime: Int, nightCharge: Boolean): IO[Int] =
    insertUsers(UUID.randomUUID(), name, sleepingTime, wakeUpTime, nightCharge).run.transact(ta)

  def addDevice(userId: UUID, name: String, chargingTime: Double): IO[Int] =
    insertDevices(UUID.randomUUID(), userId, name, chargingTime).run.transact(ta)

  def updateUserSleepingTime(name: String, sleepingTime: Int): IO[Int] =
    updateSleepingTime(name, sleepingTime).run.transact(ta)

  def updateUserWakeUpTime(name: String, wakeupTime: Int): IO[Int] =
    updateWakeUpTime(name, wakeupTime).run.transact(ta)

  def updateUserNightCharge(name: String, nightCharge: Boolean): IO[Int] =
    updateNightCharge(name, nightCharge).run.transact(ta)

  def getUserID(username: String): IO[UUID] =
    getUserUUID(username).transact(ta)

  def getDeviceID(userName: UUID, deviceName: String): IO[Boolean] =
    getDeviceUUID(userName, deviceName).transact(ta)

  def getUserSetting(userName: String): IO[(String, String, Boolean)] =
    getSettings(userName).transact(ta)
}
