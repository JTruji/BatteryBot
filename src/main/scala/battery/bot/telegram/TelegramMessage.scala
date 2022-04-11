package battery.bot.telegram

import java.util.Date

case class TelegramMessage (
    message_id: Long,
    from: List[TelegramFrom],
    chat: List[TelegramChat],
    date: Date,
    text: String
  )
