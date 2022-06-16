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
    check(DevicesQueries.insertDevices(UUID.randomUUID(), UUID.randomUUID(), "patinete", 8.0d));
    check(DevicesQueries.updateChargingTime("patinete", 9.0d))
    check(DevicesQueries.existDeviceUUID(UUID.randomUUID(), "patinete"))
  }
  test("Users queries works as expected") {
    check(UsersQueries.insertUsers(UUID.randomUUID(), "user01", 22, 9, false))
    check(UsersQueries.updateSettings("user01", 23, 10, true))
    check(UsersQueries.getUserUUID("user01"))
    check(UsersQueries.getSettings("user01"))
  }
}
