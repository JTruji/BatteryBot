package database

import doobie.implicits._
import doobie.postgres.implicits._
import java.util.UUID

object UsersQueries {

    // Insert new data
    def insertUsers(id_users: UUID, name: String, sleeping_time: Int, wakeup_time: Int, night_charge: Boolean): doobie.Update0 =
      sql"""insert into users (id_users, name, sleeping_time, wakeup_time, night_charge) values ($id_users, $name, $sleeping_time, $wakeup_time, $night_charge)""".update

    // Update user data
    def updateSleepingTime(name: String, sleeping_time: Int): doobie.Update0 =
      sql"""update users set sleeping_time = $sleeping_time where name = $name""".update

    def updateWakeUpTime(name: String, wakeup_time: Int): doobie.Update0 =
      sql"""update users set wakeup_time = $wakeup_time where name = $name""".update

    def updateNightCharge(name: String, night_charge: Boolean): doobie.Update0 =
      sql"""update users set night_charge = $night_charge where name = $name""".update

  }

