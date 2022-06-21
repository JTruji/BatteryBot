package battery.bot.database

import battery.bot.core.models.UserSettings

import java.util.UUID
import doobie.implicits._
import doobie.postgres.implicits._

object UsersQueries {

  // Insert new data
  def insertUsers(
      idUsers: UUID,
      chatId: Long,
      sleepingTime: Int,
      wakeupTime: Int,
      nightCharge: Boolean
  ): doobie.Update0 =
    sql"""insert into users (id_users, chat_id, sleeping_time, wakeup_time, night_charge) values ($idUsers, $chatId, $sleepingTime, $wakeupTime, $nightCharge) on conflict do nothing""".update

  // Update user data
  def updateSettings(userUUID:UUID, sleepingTime: Int, wakeupTime: Int, nightCharge: Boolean): doobie.Update0 =
    sql"""update users set sleeping_time = $sleepingTime, wakeup_time = $wakeupTime, night_charge = $nightCharge where id_users = $userUUID""".update

  // Get user UUID
  def getUserId(chatId: Long): doobie.Query0[UUID] =
    sql"""select id_users from users where chat_id = $chatId""".query[UUID]

  def getSettings(userUUID:UUID): doobie.Query0[UserSettings] =
    sql"""select sleeping_time, wakeup_time, night_charge from users where id_users = $userUUID"""
      .query[UserSettings]

}
