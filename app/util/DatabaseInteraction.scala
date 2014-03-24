package util

import util.Driver.simple._
import util.Driver.profile.backend.DatabaseDef

object DatabaseInteraction {
  val dbSession: DatabaseDef = {
    val dbUrl    = Configurations getConfig "db.default.url"
	val dbDriver = Configurations getConfig "db.default.driver"
    val dbUser   = Configurations getConfig "db.default.user"
    val dbPwd    = Configurations getConfig "db.default.password"
    
    Database.forURL(dbUrl, driver = dbDriver, user = dbUser, password = dbPwd)
  }
}
