package ch.makery.fishGame.view

import ch.makery.fishGame.MainApp
import scalafx.event.ActionEvent
import scalafx.scene.control.{Button, TextField}
import scalafx.scene.layout.AnchorPane
import scalafxml.core.macros.sfxml

@sfxml
class InstructionsController(
                              private val usernameField: TextField,
                              private val startButton: Button,
                              private val root: AnchorPane
                            ) {

  // Initialize the components after they are injected
  def initialize(): Unit = {
    if (usernameField != null && startButton != null) {
      // Disable the Start button if the text field is empty
      usernameField.text.onChange { (_, _, newValue) =>
        Option(startButton).foreach(_.disable = newValue.trim.isEmpty)
      }

      // Initially disable the button if the field is empty
      startButton.disable = usernameField.text.value.trim.isEmpty
    } else {
      println("usernameField or startButton is null in InstructionsController")
    }
  }

  // Show game screen when 'Start Game' button is pressed
  def handleStartGame(action: ActionEvent): Unit = {
    if (usernameField != null) {
      val username = usernameField.text.value.trim
      if (username.nonEmpty) {
        // Switch to the game screen
        println("StartGame button pressed!")
        MainApp.showGame(username: String)
      }
    }
  }
}
