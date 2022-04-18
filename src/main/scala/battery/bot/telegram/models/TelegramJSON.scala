package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class TelegramJSON(
    ok: Boolean,
    result: List[TelegramResult]
)

object TelegramJSON{
  implicit val codec: Codec[TelegramJSON] = deriveConfiguredCodec[TelegramJSON]
}