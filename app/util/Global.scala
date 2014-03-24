package util

import play.api.Play
import play.api.Application
import play.api.GlobalSettings
import scala.slick.jdbc.meta.MTable

import util.Driver.simple._
import util.DatabaseInteraction.dbSession
import models.Publications

object Global extends GlobalSettings {
  override def onStart(app : Application) = {
    super.onStart(app)
    
    dbSession withSession { implicit session =>
        val publications = TableQuery[Publications]
        if (MTable.getTables("publications").list().isEmpty)
          publications.ddl.create
    }
  }
}
