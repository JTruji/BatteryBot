package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class TelegramResult(
    updateId: Long,
    message: TelegramMessage
)

object TelegramResult {
  implicit val codec: Codec[TelegramResult] = deriveConfiguredCodec[TelegramResult]
}
