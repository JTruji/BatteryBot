package database

import doobie.implicits._
import doobie.postgres.implicits._
import java.time.Instant
import java.util.UUID

object Queries {
  // Insert new data
  def insertUsers(id_users: UUID, name: String, sleeping_time: Int, wakeup_time: Int, night_charge: Boolean) =
    sql"""insert into users (id_users, name, sleeping_time, wakeup_time, night_charge)
    values ($id_users, $name, $sleeping_time, $wakeup_time, $night_charge)""".update

  def insertDevices(id_device: UUID, name: String, charging_time: Double) =
    sql"""insert into devices (id_device, name, charging_time)
    values ($id_device, $name, $charging_time)""".update

  def insertPrices(time_range: Instant, price: BigDecimal) =
    sql"""insert into prices (time_range, price)
    values ($time_range, $price)""".update

  // Update user data
  def updateSleepingTime(name: String, sleeping_time: Int) =
    sql"""update users set sleeping_time = $sleeping_time where name = $name""".update

  def updateWakeUpTime(name: String, wakeup_time: Int) =
    sql"""update users set wakeup_time = $wakeup_time where name = $name""".update

  def updateNightCharge(name: String, night_charge: Boolean) =
    sql"""update users set night_charge = $night_charge where name = $name""".update

  // Update devices data
  def updateChargingTime(name: String, charging_time: Double) =
    sql"""update devices set charging_time = $charging_time where name = $name""".update
}
