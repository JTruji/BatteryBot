package battery.bot.telegram

import io.circe.generic.extras.Configuration

package object models {
  implicit val customConfig: Configuration = Configuration.default.withSnakeCaseMemberNames
}
