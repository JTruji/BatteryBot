package battery.bot.database

import cats.effect.IO
import doobie.Transactor
import doobie.implicits._
import java.math.BigDecimal
import java.time.Instant

class PersistenceService(ta: Transactor[IO]) {

  def addPrice(hour: Int, price: BigDecimal): IO[Int] ={
    PricesQueries.insertPrices(Instant.now(), price).run.transact(ta)
  }
  def addScraperPrices(pricesList:List[(Instant,BigDecimal)]): IO[Int] ={
      PricesQueries.insertManyPrices.updateMany(pricesList).transact(ta)
  }

  //insertDevices().run.transact(ta)

  //insertPrices(, 0.123).run.transact(ta)

}

