package models

import util.db.Driver.simple._

case class Migration(id: Long, migrationName: String, hasFinished: Boolean)

class Migrations(tag: Tag) extends Table[Migration](tag, "migrations") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def migrationName = column[String]("migration_name")
  def hasFinished = column[Boolean]("has_finished")
  
  def * = (id, migrationName, hasFinished) <> (Migration.tupled, Migration.unapply)
}
