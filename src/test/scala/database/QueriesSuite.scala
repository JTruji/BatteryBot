package database

import doobie.munit.analysisspec.IOChecker

import java.time.Instant
import java.util.UUID

class QueriesSuite extends PersistenceBaseSuite with IOChecker {

  test("Queries works as expected") {
    check(PricesQueries.insertPrices(Instant.now(), 0.324));
    check(DevicesQueries.insertDevices(UUID.randomUUID(), "patinete", 8.0d));
    check(UsersQueries.insertUsers(UUID.randomUUID(), "user01", 22, 9, false));
    check(UsersQueries.updateSleepingTime("user01", 23));
    check(UsersQueries.updateWakeUpTime("user01", 10));
    check(UsersQueries.updateNightCharge("user01", true));
    check(DevicesQueries.updateChargingTime("patinete", 9.0d))
  }

}
