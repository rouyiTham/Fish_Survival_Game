package ch.makery.fishGame.util

import scalikejdbc._
import ch.makery.fishGame.model.Player

trait Database {
  val derbyDriverClassname = "org.apache.derby.jdbc.EmbeddedDriver"
  val dbURL = "jdbc:derby:fishGameDB;create=true;";

  // Initialize JDBC driver & connection pool
  Class.forName(derbyDriverClassname)
  ConnectionPool.singleton(dbURL, "ry", "mine")

  // Ad-hoc session provider on the REPL
  implicit val session = AutoSession
}

object Database extends Database {

  def setupDB(): Unit = {
    DB.autoCommit { implicit session =>
      if (!hasDBInitialize) {
        println("Initializing database...")
        Player.initializeTable()
      } else {
        println("Database already initialized.")
      }
    }
  }

  def hasDBInitialize: Boolean = {
    DB.readOnly { implicit session =>
      sql"SELECT 1 FROM SYS.SYSTABLES WHERE TABLENAME = UPPER('LEADERBOARD')".map(_.int(1)).single.apply().isDefined
    }
  }

}

