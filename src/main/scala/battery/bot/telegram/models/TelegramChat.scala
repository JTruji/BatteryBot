package battery.bot.telegram.models

import io.circe.{Codec, Decoder, Encoder}
import io.circe.generic.extras.semiauto.deriveConfiguredCodec


case class TelegramChat(
    id: Long,
    first_name: Option[String],
    username: Option[String]
)

object TelegramChat {
//    implicit val codec: Codec[TelegramChat] = deriveConfiguredCodec[TelegramChat]
  implicit val encoderTelegramChat: Encoder[TelegramChat] = Encoder.forProduct3(
    "id",
    "firstName",
    "username"
  )(u => (u.id, u.first_name, u.username))

  implicit val decoderTelegramChat = Decoder.forProduct3(
    "id",
    "first_name",
    "username"
  )(TelegramChat.apply)
}