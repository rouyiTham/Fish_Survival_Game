package ch.makery.fishGame.model

import ch.makery.fishGame.util.Database
import scalikejdbc._

case class Player(username: String, survivalTime: Long)

object Player extends Database {

  def apply(rs: WrappedResultSet): Player = new Player(
    rs.string("username"),
    rs.long("survivalTime")
  )

  def initializeTable()(implicit session: DBSession = AutoSession): Unit = {
    println("Checking if Leaderboard table exists...")
    if (!tableExists("LEADERBOARD")) {
      println("Creating Leaderboard table...")
      sql"""
    CREATE TABLE LEADERBOARD (
      ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
      USERNAME VARCHAR(64) NOT NULL,
      SURVIVALTIME BIGINT NOT NULL,
      PRIMARY KEY (ID)
    )
    """.execute.apply()
      println("Leaderboard table created successfully.")
    } else {
      println("Leaderboard table already exists.")
    }
  }

  def tableExists(tableName: String)(implicit session: DBSession): Boolean = {
    val result = sql"""
    SELECT COUNT(*) FROM SYS.SYSTABLES
    WHERE TABLENAME = ${tableName.toUpperCase} AND TABLETYPE = 'T'
  """.map(_.int(1)).single.apply()
    result.exists(_ > 0)
  }

  def insert(player: Player)(implicit session: DBSession = AutoSession): Unit = {
    sql"""
    INSERT INTO LEADERBOARD (USERNAME, SURVIVALTIME)
    VALUES (${player.username}, ${player.survivalTime})
    """.update.apply()
      println(s"Player ${player.username} inserted successfully.")

  }

  def topFivePlayers()(implicit session: DBSession = AutoSession): List[Player] = {
    sql"""
    SELECT USERNAME, SURVIVALTIME FROM LEADERBOARD
    ORDER BY SURVIVALTIME DESC FETCH FIRST 5 ROWS ONLY
  """.map(rs => Player(rs.string("USERNAME"), rs.long("SURVIVALTIME"))).list.apply()
  }

}
