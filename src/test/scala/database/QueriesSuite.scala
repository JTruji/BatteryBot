package database

import doobie.munit.analysisspec.IOChecker

import java.time.Instant
import java.util.UUID

class QueriesSuite extends PersistenceBaseSuite with IOChecker {

  test("Queries works as expected") {
    check(Queries.insertPrices(Instant.now(), 0.324));
    check(Queries.insertDevices(UUID.randomUUID(), "patinete", 8.0d));
    check(Queries.insertUsers(UUID.randomUUID(), "user01", 22, 9, false));
    check(Queries.updateSleepingTime("user01",23));
    check(Queries.updateWakeUpTime("user01",10));
    check(Queries.updateNightCharge("user01",true));
    check(Queries.updateChargingTime("patinete",9.0d))
  }

}