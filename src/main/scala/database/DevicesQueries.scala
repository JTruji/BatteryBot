package database

import doobie.implicits._
import doobie.postgres.implicits._
import java.util.UUID

object DevicesQueries {
    def insertDevices(id_device: UUID, name: String, charging_time: Double): doobie.Update0 =
      sql"""insert into devices (id_device, name, charging_time) values ($id_device, $name, $charging_time)""".update

    def updateChargingTime(name: String, charging_time: Double): doobie.Update0 =
      sql"""update devices set charging_time = $charging_time where name = $name""".update
  }

