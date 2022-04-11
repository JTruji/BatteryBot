package battery.bot.telegram

case class TelegramResult (
  update_id: Long,
  message: List[TelegramMessage]
)
