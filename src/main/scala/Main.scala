import config.Config
import pureconfig._
import pureconfig.generic.auto._

ConfigSource.default.load[ServiceConf]
// res4: ConfigReader.Result[ServiceConf] = Right(
//   ServiceConf(
//     "example.com",
//     Port(8080),
//     true,
//     List(PrivateKey(/home/user/myauthkey), Login("pureconfig", "12345678"))
//   )
// )

object Main {
  def main(args: Array[String]): Unit = {
    println("Hello fuecoco")
  }
}
