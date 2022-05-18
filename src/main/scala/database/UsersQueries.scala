package database

import doobie.implicits._
import doobie.postgres.implicits._
import java.util.UUID

object UsersQueries {

  // Insert new data
  def insertUsers(
      idUsers: UUID,
      name: String,
      sleepingTime: Int,
      wakeupTime: Int,
      nightCharge: Boolean
  ): doobie.Update0 =
    sql"""insert into users (id_users, name, sleeping_time, wakeup_time, night_charge) values ($idUsers, $name, $sleepingTime, $wakeupTime, $nightCharge)""".update

  // Update user data
  def updateSleepingTime(name: String, sleepingTime: Int): doobie.Update0 =
    sql"""update users set sleeping_time = $sleepingTime where name = $name""".update

  def updateWakeUpTime(name: String, wakeupTime: Int): doobie.Update0 =
    sql"""update users set wakeup_time = $wakeupTime where name = $name""".update

  def updateNightCharge(name: String, nightCharge: Boolean): doobie.Update0 =
    sql"""update users set night_charge = $nightCharge where name = $name""".update

}
