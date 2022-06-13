package battery.bot.core.models

case class UserSettings(
    sleepingTime: Int,
    wakeUpTime: Int,
    nightCharge: Boolean
)
