package battery.bot.database

import battery.bot.core.models.UserSettings

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
  def updateSettings(name: String, sleepingTime: Int, wakeupTime: Int, nightCharge: Boolean): doobie.Update0 =
    sql"""update users set sleeping_time = $sleepingTime, wakeup_time = $wakeupTime, night_charge = $nightCharge where name = $name""".update

  // Get user UUID
  def getUserUUID(username: String): doobie.Query0[UUID] =
    sql"""select id_users from users where name = $username""".query[UUID]

  def getSettings(username: String): doobie.Query0[UserSettings] =
    sql"""select sleeping_time, wakeup_time, night_charge from users where name = $username"""
      .query[UserSettings]

}
