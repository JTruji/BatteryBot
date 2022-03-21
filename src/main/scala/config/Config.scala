package config

case class DatabaseConfig (
  url: String,
  user: String,
  password: String,
  driver: String
)

case class Config (
  telegramToken: String,
  chatId: String,
  database: DatabaseConfig
)
