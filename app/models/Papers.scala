package models

import util.db.Driver.simple._

case class Paper(id: Long, authors: String, title: String, details: String, abstractLink: String, pdfLink: String)

class Papers(tag: Tag) extends Table[Paper](tag, "papers") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def authors = column[String]("authors")
  def title = column[String]("title")
  def details = column[String]("details")
  def abstractLink = column[String]("abstract_link")
  def pdfLink = column[String]("pdf_link")
  
  def * = (id, authors, title, details, abstractLink, pdfLink) <> (Paper.tupled, Paper.unapply)
}
