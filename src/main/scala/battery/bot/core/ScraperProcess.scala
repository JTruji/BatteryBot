package battery.bot.core

import battery.bot.database.PersistenceService
import battery.bot.webscraping.Scraper
import cats.effect.IO

class ScraperProcess(persistenceService: PersistenceService) {

  def getNewPrices: IO[Unit] = {
    for {
      list <- Scraper.scraperPrice
      _ <- persistenceService.addScraperPrices(list)
    } yield ()
  }
}
