package util.db.loader

import util.db.Driver.simple._
import util.db.DatabaseInteraction.dbSession
import models._

import scala.slick.jdbc.meta.MTable

object CreateTables {
  
  private var migrationTableExisted = true
  
  def didMigrationTableExist: Boolean = migrationTableExisted
  
  def createTables {

    dbSession withSession { implicit session =>
    
      // Create the mapping from table-names to corresponding TableQuery's
      val tableQueryMapping = Map(
        "papers" -> TableQuery[Papers],
        "books" -> TableQuery[Books],
        "research_areas" -> TableQuery[ResearchAreas],
        "research_area_details" -> TableQuery[ResearchAreaDetails],
        "technical_reports" -> TableQuery[TechnicalReports],
        "migrations" -> TableQuery[Migrations],
        "users" -> TableQuery[Users]
      )
      
      // Check whether the migration table exists
      if (MTable.getTables("migrations").list().isEmpty)
        migrationTableExisted = false
    
      // Create tables that don't yet exist
      for ((tableName, tableQuery) <- tableQueryMapping)
        if (MTable.getTables(tableName).list().isEmpty)
          tableQuery.ddl.create
      
      // Migrate table data
      MigrateData migrateData
    }
  }
}