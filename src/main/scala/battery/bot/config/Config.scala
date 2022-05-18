package battery.bot.config

case class DatabaseConfig(
    url: String,
    user: String,
    password: String,
    driver: String
)

case class Config(
    telegramToken: String,
    database: DatabaseConfig
)
