package battery.bot.telegram.models

import io.circe.Codec
import io.circe.generic.extras.semiauto.deriveConfiguredCodec

case class TelegramResult[A](
    ok: Boolean,
    description: Option[String],
    result: List[A]
)

object TelegramResult{
  implicit def codec[A:Codec]: Codec[TelegramResult[A]] = deriveConfiguredCodec[TelegramResult[A]]
}