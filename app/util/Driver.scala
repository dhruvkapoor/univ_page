package util

import scala.slick.driver.JdbcProfile
import scala.slick.driver.H2Driver
import scala.slick.driver.PostgresDriver
import play.api._
import scala.slick.driver.SQLiteDriver
import scala.slick.driver.MySQLDriver

case class DriverNotFoundException(reason: String) extends Exception(reason)

object Driver {
  val simple = profile.simple
  lazy val profile : JdbcProfile = {
    val driverConfig = Configurations getConfig "db.default.driver"
    driverConfig match {
    	case "org.postgresql.Driver" => PostgresDriver
    	case "org.h2.Driver" => H2Driver
    	case "org.sqlite.JDBC" => SQLiteDriver
    	case "com.mysql.jdbc.Driver" => MySQLDriver
    	case _ => throw DriverNotFoundException("Driver not found for driver setting " + driverConfig)
    }
  }
}