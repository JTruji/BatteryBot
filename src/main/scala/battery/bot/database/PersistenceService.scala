package battery.bot.database

import cats.effect.IO
import doobie.Transactor
import doobie.implicits._
import java.math.BigDecimal
import java.time.Instant

class PersistenceService(ta: Transactor[IO]) {

  def addScraperPrices(pricesList:List[(Instant,BigDecimal)]): IO[Int] ={
      PricesQueries.insertManyPrices.updateMany(pricesList).transact(ta)
  }

}

