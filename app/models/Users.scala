package models

import util.db.Driver.simple._
import util.db.DatabaseInteraction.dbSession
import scala.slick.lifted.TableQuery

case class User(id: Long, username: String, password: String)

class Users(tag: Tag) extends Table[User](tag, "users") {
  def id = column[Long]("id", O.PrimaryKey, O.AutoInc)
  def username = column[String]("username")
  def password = column[String]("password")
  
  def * = (id, username, password) <> (User.tupled, User.unapply)
}

object Users {
  def authorized(username: String, password: String): Boolean = {
    dbSession withSession { implicit session =>
      val users = TableQuery[Users]
      users.filter(_.username === username).filter(_.password === password).list.length > 0
    }
  }
}
