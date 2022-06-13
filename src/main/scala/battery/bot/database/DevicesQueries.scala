package battery.bot.database

import java.util.UUID
import doobie.implicits._
import doobie.postgres.implicits._

object DevicesQueries {
  def insertDevices(idDevice: UUID, userId: UUID, name: String, chargingTime: Double): doobie.Update0 =
    sql"""insert into devices (id_device, id_user, name, charging_time) values ($idDevice, $userId, $name, $chargingTime) on conflict do nothing""".update

  def updateChargingTime(name: String, chargingTime: Double): doobie.Update0 =
    sql"""update devices set charging_time = $chargingTime where name = $name""".update

  // Get device UUID
  def existDeviceUUID(userName: UUID, deviceName: String): doobie.Query0[Boolean] =
    sql"""select exists (select 1 from devices where name = $deviceName and id_user = $userName )"""
      .query[Boolean]

}
