package battery.bot.database

import java.util.UUID
import doobie.implicits._
import doobie.postgres.implicits._

object DevicesQueries {
  def insertDevices(idDevice: UUID, userId: UUID, name: String, chargingTime: Double): doobie.Update0 =
    sql"""insert into devices (id_device, id_user, name, charging_time) values ($idDevice, $userId, $name, $chargingTime)""".update

  def updateChargingTime(name: String, chargingTime: Double, userId: UUID): doobie.Update0 =
    sql"""update devices set charging_time = $chargingTime where name = $name and id_user = $userId""".update

  // Get device UUID
  def userDevicesName(userId: UUID): doobie.Query0[String] =
    sql"""select name from devices where id_user = $userId"""
      .query[String]

  // Delete device UUID
  def deleteDevice(userId: UUID, name: String): doobie.Update0 =
    sql"""delete from devices where id_user = $userId and name = $name"""
      .update
}
