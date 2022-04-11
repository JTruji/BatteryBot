package database

import doobie.implicits._
import munit.CatsEffectSuite

class Specs2Spec extends CatsEffectSuite {

  case class prices(hour: Int, price: BigDecimal)
//
//  test(name = "data displayed")
//  def takesLongerThan(limitHour: Int) =
//    sql"select hour, price from devices where hour > $limitHour".query[prices]
//
//  test(name = "data can be updated")
//  def updatePrices(newPrice: BigDecimal, hour: Int) =
//    sql"update devices set price = $newPrice where hour = $hour".update
}

//       val trivial =
//          sql"""
//            select 42, 'foo'::varchar
//          """.query[(Int, String)]