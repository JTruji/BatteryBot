import cats.effect.IO
import config.Config
import munit.CatsEffectSuite
import pureconfig.ConfigSource
import pureconfig.generic.auto._

class ConfigSpec extends CatsEffectSuite{
  test(name ="Config can be decoded from application.conf "){
    IO(ConfigSource.default.loadOrThrow[Config]).attempt.map(_.isRight).assert
  }
}

