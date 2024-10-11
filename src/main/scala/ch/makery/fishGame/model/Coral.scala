package ch.makery.fishGame.model

import scalafx.scene.image.Image
import scalafx.scene.image.ImageView
import scalafx.scene.Scene

class Coral(imagePath: String, scene: Scene, isInverted: Boolean, initialX: Double, initialY: Double) extends ImageView {
  image = new Image(getClass.getResourceAsStream(imagePath))
  fitWidth = 60
  fitHeight = 300

  if (isInverted) {
    // Vertically flip the image
    scaleY = -1
  }

  translateX = initialX
  translateY = initialY

  def addToScene(pane: scalafx.scene.layout.Pane): Unit = {
    pane.children.add(this)
  }

  def isOffScreen: Boolean = {
    translateX.value + fitWidth.value < 0
  }

  def moveLeft(speed: Double): Unit = {
    translateX.value -= speed
  }
}

