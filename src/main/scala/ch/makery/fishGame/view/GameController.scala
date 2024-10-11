package ch.makery.fishGame.view

import ch.makery.fishGame.MainApp
import ch.makery.fishGame.model.{Coral, Fish, Player}
import scalafx.scene.layout.Pane
import scalafx.scene.Scene
import scalafx.Includes._
import javafx.{scene => jfxs}
import javafx.fxml.FXML
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.application.Platform
import scalafx.event.ActionEvent
import scalafx.scene.control.{Alert, ButtonType, Label}
import scalafx.scene.input.{KeyCode, KeyEvent}
import scalafx.util.Duration
import scala.collection.mutable.ListBuffer

class GameController {
  @FXML private var _gamePane: jfxs.layout.Pane = _
  @FXML private var _root: jfxs.layout.Pane = _
  @FXML private var _timeLabel: javafx.scene.control.Label = _

  private var fish: Option[Fish] = None
  private val corals = ListBuffer[Coral]()
  private val coralGap = 150.0 // Space between top and bottom coral pairs
  private val coralSpeed = 4.0 // Speed at which corals move left

  private var gameRunning = true
  private var player: Option[Player] = None

  // Timer variables
  private var startTime: Long = 0L
  private var timeline: Timeline = _

  def gamePane: Option[Pane] = Option(_gamePane).map(new Pane(_))
  def root: Option[Pane] = Option(_root).map(new Pane(_))

  //initialize components and start the game
  def startGame(username: String): Unit = {
    player = Some(Player(username, 0L)) // 0L as the initial survival time
    for {
      r <- root
      scene <- Option(r.scene.value)
    } {
      fish = Some(new Fish(scene))
      gamePane.foreach { pane =>
        pane.children.add(fish.get)
        fish.foreach(_.startSwimming())

        // Initialize coral generation and movement timeline
        val coralTimeline = new Timeline {
          cycleCount = Timeline.Indefinite
          keyFrames = Seq(
            KeyFrame(Duration(1000), onFinished = _ => {
              if (gameRunning) {
                val newCorals = generateCoralPair(scene)
                corals ++= newCorals
                newCorals.foreach(_.addToScene(pane))
              }
            })
          )
        }
        coralTimeline.play()

        // Timeline to move corals and remove off-screen corals
        val moveTimeline = new Timeline {
          cycleCount = Timeline.Indefinite
          keyFrames = Seq(
            KeyFrame(Duration(16), onFinished = _ => {
              if (gameRunning) {
                corals.foreach(_.moveLeft(coralSpeed))
                corals --= corals.filter(_.isOffScreen)
                checkCollisions()
              }
            })
          )
        }
        moveTimeline.play()

        // Start the stopwatch
        startTime = System.currentTimeMillis()
        timeline = new Timeline {
          cycleCount = Timeline.Indefinite
          keyFrames = Seq(
            KeyFrame(Duration(1000), onFinished = (event: ActionEvent) => {
              updateStopwatch()
            })
          )
        }
        timeline.play()
      }

      // Set up key event handling
      scene.onKeyPressed = (event: KeyEvent) => handleKeyPressed(event)
    }
  }

  // Update the stopwatch label
  private def updateStopwatch(): Unit = {
    val elapsedTime = (System.currentTimeMillis() - startTime) / 1000
    player = player.map(p => p.copy(survivalTime = elapsedTime)) //Create a new player instance with updated survivalTime
    _timeLabel.setText(s"Time: ${elapsedTime}s")
  }

  // Move the fish when space bar is pressed and game is running
  private def handleKeyPressed(event: KeyEvent): Unit = {
    if (event.code == KeyCode.Space && gameRunning) {
      fish.foreach(_.moveUp())
    }
  }

  // Generate a pair of corals (top and bottom) with a constant gap
  private def generateCoralPair(scene: Scene): Seq[Coral] = {
    val maxCoralHeight = scene.height.value - coralGap - 100
    val randomHeight = 100 + Math.random() * (maxCoralHeight - 100)

    val bottomCoral = new Coral("/images/coral.png", scene, isInverted = false, scene.width.value, scene.height.value - randomHeight)
    val topCoral = new Coral("/images/coral.png", scene, isInverted = true, scene.width.value, 0)

    bottomCoral.fitHeight = randomHeight
    topCoral.fitHeight = scene.height.value - (randomHeight + coralGap)

    Seq(bottomCoral, topCoral)
  }

  // Check for collisions and handle game over
  private def checkCollisions(): Unit = {
    fish.foreach { f =>
      // Check collision with corals
      corals.foreach { c =>
        if (isColliding(f, c)) {
          endGame()
        }
      }

      // Check if fish hits the seabed
      if (f.translateY.value >= f.scene.value.height.value - f.fitHeight.value) {
        f.translateY.value = f.scene.value.height.value - f.fitHeight.value
        endGame()
      }
    }
  }

  // Determine if the fish is colliding with the coral
  private def isColliding(fish: Fish, coral: Coral): Boolean = {

    //shrinking the bounding boxes for more precise collision detection
    val fishBounds = (
      fish.translateX.value + 10,
      fish.translateY.value + 10,
      fish.fitWidth.value - 15,
      fish.fitHeight.value - 15)

    val coralBounds = (
      coral.translateX.value + 10,
      coral.translateY.value + 10,
      coral.fitWidth.value - 15,
      coral.fitHeight.value - 15)

    // Check if fish and coral overlap
    fishBounds._1 < coralBounds._1 + coralBounds._3 &&
      fishBounds._1 + fishBounds._3 > coralBounds._1 &&
      fishBounds._2 < coralBounds._2 + coralBounds._4 &&
      fishBounds._2 + fishBounds._4 > coralBounds._2
  }

  // End the game and stop all activities
  private def endGame(): Unit = {
    gameRunning = false
    fish.foreach(_.stopSwimming())
    timeline.stop()

    val finalTime = player.map(_.survivalTime).getOrElse(0L)

    // Insert player's score into the database
    player.foreach { p =>
      Player.insert(p.copy(survivalTime = finalTime))
    }

    println("current top 5: ", Player.topFivePlayers())

    // Show the game over dialog with the score
    showGameOverDialog(finalTime)
  }

  // Show the game over dialog with the player's score
  private def showGameOverDialog(finalTime: Long): Unit = {
    Platform.runLater {
      val replayButtonType = new ButtonType("Replay")
      val quitButtonType = new ButtonType("Quit")

      val alert = new Alert(Alert.AlertType.Information) {
        initOwner(root.get.getScene.getWindow)
        title = "Game Over"
        headerText = "Your Score"
        contentText = s"Time Survived: $finalTime seconds"
        buttonTypes = Seq(replayButtonType, quitButtonType)
      }

      val result = alert.showAndWait()

      result match {
        case Some(`replayButtonType`) =>
        // Redirect to Instructions page to restart a new game
        MainApp.showInstructions()

        case Some(`quitButtonType`) =>
        // Redirect to Welcome page to quit the game
        MainApp.showWelcome()

        case _ =>
          // do nothing or handle unexpected cases
      }
    }
  }
}


