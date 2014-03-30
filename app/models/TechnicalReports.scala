package models

import util.db.Driver.simple._

case class TechnicalReport(technicalReportId: String, authors: String, title: String, date: String, pages: Int, reportLink: String)

class TechnicalReports(tag: Tag) extends Table[TechnicalReport](tag, "technical_reports") {
  def technicalReportId = column[String]("technical_report_id", O.PrimaryKey)
  def authors = column[String]("authors")
  def title = column[String]("title")
  def date = column[String]("date")
  def pages = column[Int]("pages")
  def reportLink = column[String]("report_link")
  
  def * = (technicalReportId, authors, title, date, pages, reportLink) <> (TechnicalReport.tupled, TechnicalReport.unapply)
}
