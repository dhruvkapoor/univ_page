package util.db.loader

import play.api._
import play.api.Application
import play.api.Play.current

import util.db.Driver.simple._
import util.db.DatabaseInteraction.dbSession
import models.{Migration, Migrations}
import models.{Book, Books, Paper, Papers, ResearchArea, ResearchAreas, ResearchAreaDetail, ResearchAreaDetails, TechnicalReport, TechnicalReports}

import scala.xml.XML._
import scala.collection.mutable.ListBuffer

object MigrateData {

  def migrateData {
    
    dbSession withSession { implicit session =>
      
      // Load the migration table if it didn't exist on server launch
      if (!(CreateTables didMigrationTableExist)) {
        val file = Play getFile("/migrations/migrations.xml")
        val migrations = TableQuery[Migrations]
        val migrationsXml = loadFile(file)
        (migrationsXml \\ "migration") map { book =>
          migrations.insert(Migration(-1, 
              (book \\ "migration") text,
              false
          ))
        }
      }
      
      // Migrate books
      MigrateData migrateBooks
      
      // Migrate papers 
      MigrateData migratePapers
      
      // Migrate research areas
      MigrateData migrateResearchAreas
      
      // Migrate technical reports
      MigrateData migrateTechnicalReports
    }
  }
      
  def migrateBooks {
    
    dbSession withSession { implicit session =>
      val bookMigration = this getMigration "BOOKS"
      if (bookMigration hasFinished)
        // Return if migration has already completed
        return
      
      // Migrate books if the migration hasn't happened yet
      session.withTransaction {
        
        // Open up the publications xml
        val file = Play.getFile("/migrations/publications.xml")
        val publicationsXml = loadFile(file)
    
        // Parse into rows of the books table
        val books = TableQuery[Books]
        (publicationsXml \\ "book") map { book =>
          books insert Book(-1, 
              (book \\ "authors").text, 
              (book \\ "name").text, 
              (book \\ "details").text, 
              (book \\ "link").text
          )
	    }
        
        // Mark migration as completed
        this setMigrationFinished bookMigration
      }
    }
  }
  
  def migratePapers {
    
    dbSession withSession { implicit session =>
      val paperMigration = this getMigration "PAPERS"
      if (paperMigration hasFinished)
        // Return if migration has already completed
        return
      
      // Migrate papers if the migration hasn't happened yet
      session.withTransaction {
        
        // Open up the publications xml
        val file = Play.getFile("/migrations/publications.xml")
        val publicationsXml = loadFile(file)
    
        // Parse into rows of the papers table
        val papers = TableQuery[Papers]
        (publicationsXml \\ "paper") map { paper =>
          papers insert Paper(-1,
              (paper \\ "@category").text,
              (paper \\ "authors").text, 
              (paper \\ "title").text, 
              (paper \\ "details").text,
              (paper \\ "abstract_link").text,
              (paper \\ "pdf_link").text
          )
	    }
        
        // Mark migration as completed
        this setMigrationFinished paperMigration
      }
    }
  }
      
  def migrateResearchAreas {
    
    dbSession withSession { implicit session =>
      val researchAreaMigration = this getMigration "RESEARCH_AREAS"
      if (researchAreaMigration hasFinished)
        // Return if migration has already completed
        return
      
      // Migrate research areas if the migration hasn't happened yet
      session.withTransaction {
        
        // Open up the research areas xml
        val file = Play.getFile("/migrations/research_areas.xml")
        val researchAreasXml = loadFile(file)
    
        // Parse into rows of the research areas table
        val researchAreas = TableQuery[ResearchAreas]
        val researchAreaDetails = TableQuery[ResearchAreaDetails]
        (researchAreasXml \\ "research_area") map { researchArea =>
          val researchAreaId = (researchAreas returning researchAreas.map(_.id)) insert ResearchArea(-1, 
              (researchArea \\ "name").text, 
              (researchArea \\ "laboratory").text
          )
          
          // Read through the description fields and add to the ResearchAreaDetails table
          (researchArea \\ "details" \\ "description") map { researchAreaDescription =>
            researchAreaDetails insert ResearchAreaDetail(-1, researchAreaId, researchAreaDescription.text)
          }
	    }
        
        // Mark migration as completed
        this setMigrationFinished researchAreaMigration
      }
    }
  }
  
  def migrateTechnicalReports {
    
    dbSession withSession { implicit session =>
      val technicalReportMigration = this getMigration "TECHNICAL_REPORTS"
      if (technicalReportMigration hasFinished)
        // Return if migration has already completed
        return
        
      // Migrate technical reports if the migration hasn't happened yet
      session.withTransaction {
        
        // Open up the technical reports xml
        val file = Play.getFile("/migrations/technical_reports.xml")
        val technicalReportsXml = loadFile(file)
    
        // Parse into rows of the technical reports table
        val technicalReports = TableQuery[TechnicalReports]
        (technicalReportsXml \\ "technical_report") map { technicalReport =>
          val pages: Int = ((technicalReport \\ "pages").text) match {
            case valid if valid.length > 0 => new Integer(valid)
            case invalid => 0
          }

          technicalReports insert TechnicalReport(
              (technicalReport \\ "technical_report_id").text, 
              (technicalReport \\ "authors").text,
              (technicalReport \\ "title").text,
              (technicalReport \\ "date").text,
              pages,
              (technicalReport \\ "report_link").text
          )
        }
        
        // Mark migration as completed
        this setMigrationFinished technicalReportMigration
      }
    }
  }
  
  private def getMigration(tableKey: String): Migration = {
    dbSession withSession { implicit session =>
      val migrations = TableQuery[Migrations]      
      migrations.filter(_.migrationName === tableKey).list.head
    }
  }
  
  private def setMigrationFinished(migration: Migration) {
    dbSession withSession { implicit session =>
      val migrations = TableQuery[Migrations]
      migrations filter(_.id === migration.id) map(_.hasFinished) update(true)
    }
  }
}