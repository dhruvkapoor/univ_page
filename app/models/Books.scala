package models

import util.db.Driver.simple._

case class Book(id: Long, authors: String, name: String, details: String, link: String)

class Books(tag: Tag) extends Table[Book](tag, "books") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def authors = column[String]("authors")
  def name = column[String]("name")
  def details = column[String]("details")
  def link = column[String]("link")
  
  def * = (id, authors, name, details, link) <> (Book.tupled, Book.unapply)
}
