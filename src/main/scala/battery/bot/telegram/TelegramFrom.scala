package battery.bot.telegram

case class TelegramFrom (
    id: Long,
    is_bot: Boolean,
    first_name: String,
    username: String,
    language_code: String
)
