package battery.bot.database

import java.util.UUID
import doobie.implicits._
import doobie.postgres.implicits._

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

  // Get user UUID
  def getUserUUID(username: String): doobie.Query0[UUID] =
    sql"""select id_users from users where name = $username""".query[UUID]

  def getSettings(username: String): doobie.Query0[(String, String, Boolean)] =
    sql"""select sleeping_time, wakeup_time, night_charge from users where name = $username"""
      .query[(String, String, Boolean)]

}
