package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.mvc.Result
import play.api.libs.ws._

import models.{Book, Books, Paper, Papers}

import util.db.Driver.simple._
import util.db.DatabaseInteraction.dbSession
import util.Configurations

import scala.xml.XML._
import scala.collection.mutable.StringBuilder

object Application extends Controller {

  def index = Action {
    Ok(views.html.index("Your new application is ready."))
  }
  
  def publications = Action {
    dbSession withSession { implicit session =>
      val books = TableQuery[Books].list
      val journalPapers = TableQuery[Papers].filter(_.category === "journal").list
      val conferencePapers = TableQuery[Papers].filter(_.category === "conference").list
      Ok(views.html.publications(books)(journalPapers)(conferencePapers))
    }
  }
}
