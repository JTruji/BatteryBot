import doobie.implicits._
import doobie.postgres._
import doobie.postgres.implicits._
import doobie.util.update.Update
import java.sql.Timestamp
import java.time.Instant
import java.util.UUID

object DB_Queries {

  def insertUsers(id_users: UUID, name: String, sleeping_time: Int, wakeup_time: Int, night_charge: Boolean) =
    sql"insert into users (id_users, name, sleeping_time, wakeup_time, night_charge) values ($id_users, $name, $sleeping_time, $wakeup_time, $night_charge)".update

  def insertDevices(id_device: UUID, name: String, charging_time: Float) =
    sql"insert into devices (id_device, name, charging_time) values ($id_device, $name, $charging_time)".update

  def insertPrices(time_range: Instant, price: BigDecimal) =
    sql"insert into prices (time_range, price) values ($time_range, $price)".update

}
