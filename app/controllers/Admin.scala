package controllers

import play.api.mvc.{Action, Controller, RequestHeader}
import play.api.mvc.Results
import play.api.data.Form
import play.api.data.Forms._

import models.{Paper, Papers, Users}

import util.Secured
import util.db.DatabaseInteraction._
import util.db.Driver.simple._
import util.db.DatabaseInteraction.dbSession

object Admin extends Controller with Secured {

  def onUnauthorized(request: RequestHeader) = Results.Redirect(routes.Admin.login(false))
  
  lazy val loginForm = Form(
    tuple(
      "username" -> text,
      "password" -> text) verifying ("Invalid user or password", result => result match {
        case (username, password) => {
          Users.authorized(username, password)
        }
        case _ => false
      }))
  
  def login(wasWrongLogin: Boolean) = Action { implicit request =>
    Ok(views.html.login(loginForm)(wasWrongLogin))
  }
  
  def processLogin = Action { implicit request =>
    loginForm.bindFromRequest.fold(
        hasErrors => Redirect(routes.Admin.login(true)),
        user => Redirect(routes.Admin.admin).withSession("user" -> user._1)
    )
  }
  
  def logout = Action {
    Redirect(routes.Application.index).withNewSession
  }
  
  def admin = IsAuthenticated { username =>
    implicit request =>
      Ok(views.html.admin())
  }
  
  lazy val newPaperForm = Form(
    mapping(
      "category" -> text,
      "authors" -> text,
      "title" -> text,
      "details" -> text,
      "abstractLink" -> text,
      "pdfLink" -> text
    )(
      (category, authors, title, details, abstractLink, pdfLink) => 
        Paper(-1, category, authors, title, details, abstractLink, pdfLink))
      ((paper: Paper) => 
        Some(paper.category, paper.authors, paper.title, paper.details, paper.abstractLink, paper.pdfLink)))
  
  def papers = IsAuthenticated { username =>
    implicit request =>
      Ok(views.html.papersAdmin(newPaperForm))
  }
  
  def processPaper = IsAuthenticated { username =>
    implicit request =>
      	val form = newPaperForm.bindFromRequest
        play.api.Logger.logger.info("PRINTING1...")
        play.api.Logger.logger.info(form.toString)
        play.api.Logger.logger.info(form.get.toString)
        
      dbSession withSession { implicit session =>
        
        val papers = TableQuery[Papers]
        papers insert form.get
        Ok("Added new paper")
      }
  }
}
