package battery.bot.telegram.models

import io.circe.{Decoder, Encoder}

case class TelegramChat(
    id: Long,
    firstName: Option[String],
    username: Option[String],
    chatType: String
)

object TelegramChat {

  implicit val encoderTelegramChat: Encoder[TelegramChat] = Encoder.forProduct4(
    "id",
    "first_name",
    "username",
    "type"
  )(u => (u.id, u.firstName, u.username, u.chatType))

  implicit val decoderTelegramChat = Decoder.forProduct4(
    "id",
    "first_name",
    "username",
    "type"
  )(TelegramChat.apply)
}