package battery.bot.database

import battery.bot.database.DevicesQueries.{existDeviceUUID, insertDevices}
import battery.bot.database.UsersQueries.{getUserUUID, insertUsers}
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

  def getUserID(username: String): IO[UUID] =
    getUserUUID(username).unique.transact(ta)

  def existDeviceID(userName: UUID, deviceName: String): IO[Boolean] =
    existDeviceUUID(userName, deviceName).unique.transact(ta)

}
