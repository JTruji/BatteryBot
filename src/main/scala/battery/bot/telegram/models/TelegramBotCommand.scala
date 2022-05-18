package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class TelegramBotCommand(
  command: String,
  description: String
)

object TelegramBotCommand{
  implicit val codec: Codec[TelegramBotCommand] = deriveConfiguredCodec[TelegramBotCommand]
}