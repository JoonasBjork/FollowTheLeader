package ftlsimulator.gui.settings.sidebar.tabs

import ftlsimulator.Simulation
import ftlsimulator.gui.content.ViewContainer
import ftlsimulator.gui.content.ViewContainer.{editorPane, simulationPane}
import ftlsimulator.gui.settings.sidebar.{SettingsPane, SimulationTabPane}
import scalafx.Includes._
import scalafx.geometry.Insets
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Button, ButtonType, Label, Tab}
import scalafx.scene.layout.VBox

class EditorTab extends Tab {

  text = "Editor options"
  closable = false

  //Button to swap if the user wants to move the leader or the followers
  private val moveLeaderButton = new Button {
    disable <== editorPane.pressingMouse
    text = "Adjust leader"
    onAction = () => {
      editorPane.movingLeader = !editorPane.movingLeader
      if (editorPane.movingLeader) {
        text = "Adjust followers"
      } else {
        text = "Adjust leader"
      }
    }
  }

  //Exits the editor. If the changes haven't been saved, prompts the user with an alert.
  private val exitEditorButton = new Button {
    disable <== editorPane.pressingMouse
    text = "Exit editor"
    onAction = () => {
      if (!editorPane.saved) {
        val confirmAlert = new Alert(AlertType.Confirmation) {
          title = "Confirmation"
          headerText = "Do you want to leave the editor without saving?"
        }
        val alertResult = confirmAlert.showAndWait()
        alertResult match {
          case Some(ButtonType.OK) =>
            returnToSimulator()
          case _ =>
        }
      } else {
        returnToSimulator()
      }
    }
  }

  //Instructions on how to use the editor
  private val editorInfo = new Label() {
    text =
      "Press the left mouse button to add a follower\n" +
        "Drag the mouse to add a velocity to the\n" +
        "follower or the leader\n" +
        "Press the right mouse button to remove a\n" +
        "follower or to remove the velocity of the leader"
  }


  private val editorVBox = new VBox(20,
    moveLeaderButton,
    exitEditorButton,
    editorInfo
  ) {
    padding = Insets(10, 10, 10, 10)
  }

  content = editorVBox



  def returnToSimulator() = {
    Simulation.inSimulation() = true
    ViewContainer.children = simulationPane
    SettingsPane.children = SimulationTabPane
    ViewContainer.createNewEditorPane()
  }

}
