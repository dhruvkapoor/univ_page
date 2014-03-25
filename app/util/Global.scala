package util

import play.api._
import play.api.GlobalSettings
import util.db.loader.CreateTables

object Global extends GlobalSettings {
  override def onStart(app : Application) = {
    super.onStart(app)
    
    CreateTables createTables
  }
}
