package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class TelegramMessage(
    messageId: Long,
    from: TelegramFrom,
    chat: TelegramChat,
    text: String
)

object TelegramMessage{
  implicit val codec: Codec[TelegramMessage] = deriveConfiguredCodec[TelegramMessage]
}
