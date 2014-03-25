package util.db

import util.db.Driver.simple._
import util.db.Driver.profile.backend.DatabaseDef
import util.Configurations

object DatabaseInteraction {
  val dbSession: DatabaseDef = {
    val dbUrl    = Configurations getConfig "db.default.url"
	val dbDriver = Configurations getConfig "db.default.driver"
    val dbUser   = Configurations getConfig "db.default.user"
    val dbPwd    = Configurations getConfig "db.default.password"
    
    Database.forURL(dbUrl, driver = dbDriver, user = dbUser, password = dbPwd)
  }
}
