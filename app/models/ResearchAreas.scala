package models

import util.db.Driver.simple._

case class ResearchArea(id: Long, name: String, laboratory: String)

class ResearchAreas(tag: Tag) extends Table[ResearchArea](tag, "research_areas") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def name = column[String]("name")
  def laboratory = column[String]("laboratory")
  
  def * = (id, name, laboratory) <> (ResearchArea.tupled, ResearchArea.unapply)
}
