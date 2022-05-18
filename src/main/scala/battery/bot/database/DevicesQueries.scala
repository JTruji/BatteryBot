package battery.bot.database

import java.util.UUID

object DevicesQueries {
  def insertDevices(idDevice: UUID, name: String, chargingTime: Double): doobie.Update0 =
    sql"""insert into devices (id_device, name, charging_time) values ($idDevice, $name, $chargingTime)""".update

  def updateChargingTime(name: String, chargingTime: Double): doobie.Update0 =
    sql"""update devices set charging_time = $chargingTime where name = $name""".update
}
