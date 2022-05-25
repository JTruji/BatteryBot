package battery.bot.database

import cats.effect.IO
import battery.bot.database.DevicesQueries.insertDevices
import battery.bot.database.PricesQueries.insertPrices
import battery.bot.database.UsersQueries.insertUsers
import java.time.Instant
import doobie.Transactor
import doobie.implicits._
import java.math.BigDecimal
import java.time.Instant

import java.util.UUID

class PersistenceService(ta: Transactor[IO]) {

  def addScraperPrices(pricesList:List[(Instant,BigDecimal)]): IO[Int] ={
      PricesQueries.insertManyPrices.updateMany(pricesList).transact(ta)
  }

  def addPrice(date: Instant, price: BigDecimal): IO[Int] =
    insertPrices(date, price).run.transact(ta)

  def addUser(name: String, sleepingTime: Int, wakeUpTime: Int, nightCharge: Boolean): IO[Int] =
    insertUsers(UUID.randomUUID(), name, sleepingTime, wakeUpTime, nightCharge).run.transact(ta)

  def addDevice(name: String, chargingTime: Double): IO[Int] =
    insertDevices(UUID.randomUUID(), name, chargingTime).run.transact(ta)
}

