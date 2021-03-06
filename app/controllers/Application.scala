package controllers

import play.api._
import play.api.Play.current
import play.api.mvc._
import play.api.mvc.Result
import play.api.libs.ws._

import models.{Books, Paper, Papers, ResearchAreas, ResearchAreaDetails, TechnicalReports}

import util.db.Driver.simple._
import util.db.DatabaseInteraction.dbSession
import util.Configurations

import scala.xml.XML._
import scala.collection.immutable.List
import scala.collection.mutable.StringBuilder

import java.io.File
import com.google.common.base.Charsets
import com.google.common.io.Files

object Application extends Controller {
  
  def index = Action {
    dbSession withSession { implicit session =>
    	Ok(views.html.index(TableQuery[ResearchAreas].list))
    }
  }
  
  def biography = Action {
    Ok(views.html.biography(
        Files.toString(new File("static/biography.txt"), Charsets.UTF_8)))
  }
  
  def publications = Action {
    dbSession withSession { implicit session =>
      val books = TableQuery[Books].list
      val journalPapers = TableQuery[Papers].filter(_.category === "journal").list
      val conferencePapers = TableQuery[Papers].filter(_.category === "conference").list
      Ok(views.html.publications(books)(journalPapers)(conferencePapers))
    }
  }
  
  def researchArea(id: Long) = Action {
    dbSession withSession { implicit session =>
      val researchArea = TableQuery[ResearchAreas].filter(_.id === id).list.head
      val researchAreaDetails = TableQuery[ResearchAreaDetails].filter(_.researchAreaId === id).list
      Ok(views.html.researchArea(researchArea)(researchAreaDetails))
    }
  }
  
  def technicalReports = Action {
    dbSession withSession { implicit session =>
      val technicalRepors = TableQuery[TechnicalReports].list
      Ok(views.html.technicalReports(technicalRepors))
    }
  }
  
  def technicalReport(id: String) = Action {
    dbSession withSession { implicit session =>
      val technicalReport = TableQuery[TechnicalReports].filter(_.technicalReportId === id).list.head
      val technicalReportPage = Files.toString(new File("static/technical_reports/" + id + ".txt"), Charsets.UTF_8)
      Ok(views.html.technicalReport(technicalReport)(technicalReportPage))
    }
  }
  
  def help = Action {
    Ok(views.html.help("Play API"))
  }
}
