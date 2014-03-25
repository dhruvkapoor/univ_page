package util.db.loader

import play.api._
import play.api.Application
import play.api.Play.current

import util.db.Driver.simple._
import util.db.DatabaseInteraction.dbSession
import models.{Book, Books, Paper, Papers, Migration, Migrations}

import scala.xml.XML._

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
          books.insert(Book(-1, 
              (book \\ "authors").text, 
              (book \\ "name").text, 
              (book \\ "details").text, 
              (book \\ "link").text
          ))
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
          papers.insert(Paper(-1,
              (paper \\ "authors").text, 
              (paper \\ "title").text, 
              (paper \\ "details").text,
              (paper \\ "abstract_link").text,
              (paper \\ "pdf_link").text
          ))
	    }
        
        // Mark migration as completed
        this setMigrationFinished paperMigration
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