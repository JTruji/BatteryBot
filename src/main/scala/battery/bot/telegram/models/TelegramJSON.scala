package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class TelegramJSON[A](
    ok: Boolean,
    description: Option[String],
    result: List[A]
)

object TelegramJSON{
  implicit def codec[A:Codec]: Codec[TelegramJSON[A]] = deriveConfiguredCodec[TelegramJSON[A]]
}