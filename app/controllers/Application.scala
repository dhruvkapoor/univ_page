package controllers

import play.api._
import play.api.mvc._

import models.Publication
import models.Publications

import util.Driver.simple._
import util.DatabaseInteraction.dbSession
import util.Configurations

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def testPage = Action {
    dbSession withSession { implicit session =>
      val publications = TableQuery[Publications]
      publications.insert(Publication(-1, "Details of the publication"))
    }
    Ok(views.html.test("Does this")("Work?"))
  }
}
