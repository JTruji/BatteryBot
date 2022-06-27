package battery.bot.database

import java.util.UUID
import doobie.implicits._
import doobie.postgres.implicits._

object DevicesQueries {
  // Add new device
  def insertDevices(idDevice: UUID, userId: UUID, name: String, chargingTime: Double): doobie.Update0 =
    sql"""insert into devices (id_device, id_user, name, charging_time) values ($idDevice, $userId, $name, $chargingTime)""".update

  // Update device Setting
  def updateChargingTime(name: String, chargingTime: Double, userId: UUID): doobie.Update0 =
    sql"""update devices set charging_time = $chargingTime where name = $name and id_user = $userId""".update

  // Get device List
  def userDevicesName(userUUID: UUID): doobie.Query0[String] =
    sql"""select name from devices where id_user = $userUUID"""
      .query[String]

  // Get device chargingTime
  def deviceChargingTime(userUUID: UUID, deviceName: String): doobie.Query0[Double] =
    sql"""select charging_time from devices where id_user = $userUUID and name = $deviceName"""
      .query[Double]

  // Delete device
  def deleteDevice(userId: UUID, name: String): doobie.Update0 =
    sql"""delete from devices where id_user = $userId and name = $name""".update
}
