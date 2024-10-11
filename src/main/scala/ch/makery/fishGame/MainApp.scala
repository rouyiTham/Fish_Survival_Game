package ch.makery.fishGame

import ch.makery.fishGame.util.Database
import ch.makery.fishGame.view.{GameController, LeaderboardController}
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene
import scalafxml.core.{FXMLLoader, NoDependencyResolver}
import scalafx.Includes._
import javafx.{scene => jfxs}
import scalafx.scene.media.{Media, MediaPlayer}

object MainApp extends JFXApp {

  // Initialize database
  println("Setting up the database...")
  Database.setupDB()

  // Load RootLayout.fxml
  val rootResource = getClass.getResource("view/RootLayout.fxml")
  val loader = new FXMLLoader(rootResource, NoDependencyResolver)
  loader.load()
  val roots: jfxs.layout.BorderPane = loader.getRoot[jfxs.layout.BorderPane]

  // Initialize primary stage
  stage = new PrimaryStage{
    title = "Under The Sea"
    scene = new Scene {
      stylesheets += getClass.getResource("view/stylesheet.css").toString
      root = roots
    }
  }

  private var bgmPlayer: MediaPlayer = _

  // Initialize bgm for the game
  def initializeBGM(): Unit = {
    val bgmPath = getClass.getResource("/audio/game_bgm.mp3").toExternalForm
    val media = new Media(bgmPath)
    bgmPlayer = new MediaPlayer(media){
      cycleCount = MediaPlayer.Indefinite
      volume = 0.1 // set to 10% of the maximum volume
    }
  }

  def playBGM(): Unit = {
    bgmPlayer.play()
  }

  def stopBGM(): Unit = {
    bgmPlayer.stop()
  }

  def showWelcome(): Unit = {
    val resource = getClass.getResource("view/Welcome.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  def showInstructions(): Unit = {
    val resource = getClass.getResource("view/Instructions.fxml")
    val loader = new FXMLLoader(resource, NoDependencyResolver)
    loader.load()
    val roots = loader.getRoot[jfxs.layout.AnchorPane]
    this.roots.setCenter(roots)
  }

  import javafx.fxml.FXMLLoader

  def showGame(username: String): Unit = {
    try {
      val resource = getClass.getResource("view/Game.fxml")
      val loader = new FXMLLoader(resource)

      // Load the FXML file
      val roots = loader.load[javafx.scene.layout.AnchorPane]()

      // Set the loaded FXML as the center of the main layout
      this.roots.setCenter(roots)

      // Get the controller
      Option(loader.getController[GameController]) match {
        case Some(controller) =>
          controller.startGame(username)
        case None => println("Failed to load GameController")
      }
    } catch {
      case e: Exception =>
        println(s"Error loading Game.fxml: ${e.getMessage}")
        e.printStackTrace()
    }
  }

  def showLeaderboard(): Unit = {
    try {
      val resource = getClass.getResource("view/Leaderboard.fxml")
      val loader = new FXMLLoader(resource)

      // Load the FXML file
      val roots = loader.load[javafx.scene.layout.AnchorPane]()

      // Set the loaded FXML as the center of the main layout
      this.roots.setCenter(roots)

      // Get the controller
      Option(loader.getController[LeaderboardController]) match {
        case Some(controller) => controller.setLeaderboard()
        case None => println("Failed to load LeaderboardController")
      }
    } catch {
      case e: Exception =>
        println(s"Error loading Leaderboard.fxml: ${e.getMessage}")
        e.printStackTrace()
    }
  }

  //Start here when launching the app
  showWelcome()
  initializeBGM()
  playBGM()

}
