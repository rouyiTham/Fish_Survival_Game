package ch.makery.fishGame.view

import ch.makery.fishGame.MainApp
import ch.makery.fishGame.model.Player
import scalafx.scene.control.{Button, Label}
import scalafx.scene.layout.GridPane
import javafx.{scene => jfxs}
import javafx.fxml.FXML
import scalafx.geometry.{HPos, VPos}

class LeaderboardController {
  @FXML private var _leaderboardPane: jfxs.layout.GridPane = _
  @FXML private var _backButton: javafx.scene.control.Button = _

  def leaderboardPane: GridPane = new GridPane(_leaderboardPane)

  // Set up Leaderboard with the top 5 players
  def setLeaderboard(): Unit = {
    val topPlayers = Player.topFivePlayers()

    topPlayers.zipWithIndex.foreach { case (player, index) =>
      val row = index + 1
      val usernameLabel = new Label(player.username){
        id = "leaderboardContent"
      }
      val timeLabel = new Label(player.survivalTime.toString){
        id = "leaderboardContent"
      }

      // Adding labels to GridPane
      leaderboardPane.add(usernameLabel, 0, row)
      leaderboardPane.add(timeLabel, 1, row)

      // Set alignment to center for both labels
      GridPane.setHalignment(usernameLabel, HPos.Center)
      GridPane.setValignment(usernameLabel, VPos.Center)
      GridPane.setHalignment(timeLabel, HPos.Center)
      GridPane.setValignment(timeLabel, VPos.Center)
    }
  }

  // Show Welcome page when 'Back' button is pressed
  def handleBackButton: Button = new Button(_backButton){
    println("Back button pressed!")
    MainApp.showWelcome()
  }
}
