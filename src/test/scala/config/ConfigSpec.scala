package config

import battery.bot.config.Config
import cats.effect.IO
import munit.CatsEffectSuite
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class ConfigSpec extends CatsEffectSuite {
  test(name = "Config can be decoded from application.conf ") {
    IO(ConfigSource.default.load[Config]).map(res => assert(res.isRight, res))
  }
}
