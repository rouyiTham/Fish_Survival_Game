package ch.makery.fishGame.view

import ch.makery.fishGame.MainApp
import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafxml.core.macros.sfxml

@sfxml
class WelcomeController {

  // Show Instructions page when 'Start' button is pressed
  def handleStartButton(action: ActionEvent): Unit = {
    println("Start button pressed!")
    MainApp.showInstructions()
  }

  // Show Leaderboard page when 'Leaderboard' button is pressed
  def handleLeaderboardButton(action: ActionEvent): Unit = {
    println("Leaderboard button pressed!")
    MainApp.showLeaderboard()
  }

  // Exit the application when 'Exit' button is pressed
  def handleExitButton(event: ActionEvent): Unit = {
    Platform.exit()
  }
}
