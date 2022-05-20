package battery.bot.database

import cats.effect.IO
import battery.bot.database.PricesQueries.insertPrices
import doobie.Transactor
import doobie.implicits._

import java.time.Instant

class PersistenceService(ta: Transactor[IO]) {

  def addPrice(hour: Int, price: BigDecimal) ={
    insertPrices(Instant.now(), price).run.transact(ta)
  }
  //insertUsers().run.transact(ta)

  //insertDevices().run.transact(ta)

  //insertPrices(, 0.123).run.transact(ta)

}

