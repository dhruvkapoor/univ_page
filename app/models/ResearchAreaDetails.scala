package models

import util.db.Driver.simple._

case class ResearchAreaDetail(id: Long, researchAreaId: Long, description: String)

class ResearchAreaDetails(tag: Tag) extends Table[ResearchAreaDetail](tag, "research_area_details") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def researchAreaId = column[Long]("research_area_id")
  def description = column[String]("description")
  
  def researchAreaFK = {
    val researchAreas = TableQuery[ResearchAreas]
    foreignKey("research_area_fk", researchAreaId, researchAreas)(_.id)
  }
  
  def * = (id, researchAreaId, description) <> (ResearchAreaDetail.tupled, ResearchAreaDetail.unapply)
}
