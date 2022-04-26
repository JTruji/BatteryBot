package database

import battery.bot.database.Queries
import doobie.munit.analysisspec.IOChecker

import java.time.Instant
import java.util.UUID

class QueriesSuite extends PersistenceBaseSuite with IOChecker {

  test("Queries works as expected") {
    check(Queries.insertPrices(Instant.now(), 0.324));
    check(Queries.insertDevices(UUID.randomUUID(), "patinete", 8.0d));
    check(Queries.insertUsers(UUID.randomUUID(), "user01", 22, 9, false))
  }

}