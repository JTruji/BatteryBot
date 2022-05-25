package database

import battery.bot.database.{DevicesQueries, PricesQueries, UsersQueries}
import doobie.munit.analysisspec.IOChecker
import java.math.BigDecimal
import java.time.Instant
import java.util.UUID

class QueriesSuite extends PersistenceBaseSuite with IOChecker {

  test("Price queries works as expected") {
    check(PricesQueries.insertManyPrices)
  }
  test("Devices queries works as expected") {
    check(DevicesQueries.insertDevices(UUID.randomUUID(),"Paco", "patinete", 8.0d));
    check(DevicesQueries.updateChargingTime("patinete", 9.0d))
  }
  test("Users queries works as expected") {
    check(UsersQueries.insertUsers(UUID.randomUUID(), "user01", 22, 9, false));
    check(UsersQueries.updateSleepingTime("user01", 23));
    check(UsersQueries.updateWakeUpTime("user01", 10));
    check(UsersQueries.updateNightCharge("user01", true))
  }
}
