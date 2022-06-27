package battery.bot.core.models

case class Message(
    messageId: Long,
    from: From,
    chat: Chat,
    text: String
)
