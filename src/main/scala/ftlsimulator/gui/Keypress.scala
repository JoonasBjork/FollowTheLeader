package ftlsimulator.gui

import scalafx.Includes._
import scalafx.scene.Scene
import scalafx.scene.input.KeyCode

import scala.collection.mutable.Set

object Keypress {
  val keysPressed = Set[KeyCode]()

  def handleInput(scene: Scene) = {
    scene.onKeyPressed = event => keysPressed += event.code
    scene.onKeyReleased = event => keysPressed -= event.code
  }
}
