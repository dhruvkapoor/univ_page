package models

import util.Driver.simple._


case class Publication(id: Long, body: String)

class Publications(tag: Tag) extends Table[Publication](tag, "publications") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def body = column[String]("body")
  def * = (id, body) <> (Publication.tupled, Publication.unapply)
}
