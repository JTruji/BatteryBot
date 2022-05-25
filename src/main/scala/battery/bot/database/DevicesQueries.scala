package battery.bot.database

import java.util.UUID
import doobie.implicits._
import doobie.postgres.implicits._

object DevicesQueries {
  def insertDevices(idDevice: UUID,username: String, name: String, chargingTime: Double): doobie.Update0 =
    sql"""insert into devices (id_device,username, name, charging_time) values ($idDevice,$username, $name, $chargingTime)""".update

  def updateChargingTime(name: String, chargingTime: Double): doobie.Update0 =
    sql"""update devices set charging_time = $chargingTime where name = $name""".update
}
