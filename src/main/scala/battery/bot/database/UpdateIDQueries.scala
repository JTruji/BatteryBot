package battery.bot.database

import java.util.UUID
import doobie.implicits._
import doobie.postgres.implicits._
import doobie.util.fragment

import java.util.UUID
import java.time.Instant

object UpdateIDQueries {

  def updateUpdateID(updateID: Long, instant: Instant): doobie.Update0 =
    sql"""update updates set update_id = $updateID, time_range = $instant""".update

  def getUpdateID: doobie.Query0[Long] =
    sql"""select update_id from updates """.query[Long]
}
