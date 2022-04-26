package database

import cats.effect.IO
import com.dimafeng.testcontainers.PostgreSQLContainer
import com.dimafeng.testcontainers.munit.TestContainerForAll
import doobie.util.transactor.Transactor
import munit.CatsEffectSuite
import org.flywaydb.core.Flyway
import org.testcontainers.utility.DockerImageName

trait PersistenceBaseSuite extends CatsEffectSuite with TestContainerForAll {

  val driverName: String = "org.postgresql.Driver"
  val dbName: String     = "battery_bot"
  val dbUserName: String = "username"
  val dbPassword: String = "password"

  override val containerDef: PostgreSQLContainer.Def = PostgreSQLContainer.Def(
    dockerImageName = DockerImageName.parse("postgres:14.2"),
    databaseName = dbName,
    username = dbUserName,
    password = dbPassword
  )

  lazy val transactor: Transactor[IO] = withContainers(pg =>
    Transactor.fromDriverManager[IO](
      driverName, // driver classname
      pg.jdbcUrl, // connect URL (driver-specific)
      dbUserName, // user
      dbPassword  // password
    )
  )

  override def afterContainersStart(containers: Containers): Unit =
    withContainers(pg =>
      IO(
        Flyway
          .configure()
          .dataSource(pg.jdbcUrl, dbUserName, dbPassword)
          .load()
          .migrate()
      ).void.unsafeRunSync()
    )

  override def afterTest(containers: PostgreSQLContainer, throwable: Option[Throwable]): Unit =
    () //Run delete queries here is applies

}
