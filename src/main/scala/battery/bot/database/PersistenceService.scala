package battery.bot.database

import battery.bot.database.DevicesQueries.insertDevices
import battery.bot.database.UsersQueries.{getUserUUID, insertUsers}
import cats.effect.IO
import doobie.Transactor
import doobie.implicits._

import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class PersistenceService(ta: Transactor[IO]) {

  def addScraperPrices(pricesList:List[(Instant,BigDecimal)]): IO[Int] ={
      PricesQueries.insertManyPrices.updateMany(pricesList).transact(ta)
  }

  def addUser(name: String, sleepingTime: Int, wakeUpTime: Int, nightCharge: Boolean): IO[Int] = {
    insertUsers(UUID.randomUUID(), name, sleepingTime, wakeUpTime, nightCharge).run.transact(ta)
  }

  def addDevice(userId:UUID, name: String, chargingTime: Double): IO[Int] = {
    insertDevices(UUID.randomUUID(),userId, name, chargingTime).run.transact(ta)
  }

  def getUserID(username:String): IO[String] ={
    getUserUUID(username).transact(ta)
  }
}

