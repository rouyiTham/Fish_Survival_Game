package ch.makery.fishGame.model

import scalafx.scene.image.Image
import scalafx.scene.image.ImageView
import scalafx.animation.{KeyFrame, Timeline}
import scalafx.scene.Scene
import scalafx.util.Duration

class Fish(scene: Scene) extends ImageView {
  // Load the fish image
  image = new Image(getClass.getResourceAsStream("/images/fish.png"))
  fitWidth = 60
  fitHeight = 40

  // Initial position of the fish
  translateX = 100
  translateY = 300

  // Movement speed
  private val swimUpSpeed = 0.1
  private val gravity = 0.1 //speed of falling
  private var verticalSpeed = 0.0 //-ve is upwards, +ve is downwards

  // Timeline for the fish's movement
  private val movementTimeline = new Timeline {
    cycleCount = Timeline.Indefinite
    keyFrames = Seq(
      KeyFrame(
        Duration(16), // about 60 FPS
        onFinished = _ => {
          // Apply gravity when space bar is not pressed
          verticalSpeed += gravity
          translateY.value += verticalSpeed

          // Move fish to the right
          translateX.value += 0.2

          // Reset position if fish moves off-screen horizontally
          if (translateX.value > scene.width.value) {
            translateX.value = -fitWidth.value
          }
        }
      )
    )
  }

  // Start the movement
  def startSwimming(): Unit = movementTimeline.play()

  // Stop the movement
  def stopSwimming(): Unit = movementTimeline.stop()

  // Move the fish up (called when the space bar is pressed)
  def moveUp(): Unit = {
    verticalSpeed = -3.0 // upward speed
  }
}
