package database

import doobie.implicits._
import doobie.postgres.implicits._
import java.time.Instant
import java.util.UUID

object Queries {

  def insertUsers(idUsers: UUID, name: String, sleepingTime: Int, wakeupTime: Int, nightCharge: Boolean) =
    sql"""insert into users (idUsers, name, sleepingTime, wakeupTime, nightCharge)
    values ($idUsers, $name, $sleepingTime, $wakeupTime, $nightCharge)""".update

  def insertDevices(idDevice: UUID, name: String, chargingTime: Float) =
    sql"""insert into devices (idDevice, name, chargingTime)
    values ($idDevice, $name, $chargingTime)""".update

  def insertPrices(timeRange: Instant, price: BigDecimal) =
    sql"""insert into prices (time_range, price)
    values ($timeRange, $price)""".update

}
