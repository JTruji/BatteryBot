package battery.bot.database

import battery.bot.database.DevicesQueries.insertDevices
import cats.effect.IO
import battery.bot.database.PricesQueries.insertPrices
import battery.bot.database.UsersQueries.insertUsers
import doobie.Transactor
import doobie.implicits._

import java.util.{Random, UUID}
import java.time.Instant

class PersistenceService(ta: Transactor[IO]) {

  def addPrice(hour: Int, price: BigDecimal): IO[Int] ={
    insertPrices(Instant.now(), price).run.transact(ta)
  }

  def addUser(name:String, sleepingTime: Int, wakeUpTime:Int, nightCharge:Boolean): IO[Int] = {
    insertUsers(UUID.randomUUID(), name, sleepingTime, wakeUpTime, nightCharge).run.transact(ta)
  }

  def addDevice(name: String, chargingTime:Double): IO[Int] ={
    insertDevices(UUID.randomUUID(), name, chargingTime).run.transact(ta)
  }
}

