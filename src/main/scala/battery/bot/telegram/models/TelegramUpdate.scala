package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class TelegramUpdate(
    updateId: Long,
    message: Option[TelegramMessage]
)

object TelegramUpdate {
  implicit val codec: Codec[TelegramUpdate] = deriveConfiguredCodec[TelegramUpdate]
}
