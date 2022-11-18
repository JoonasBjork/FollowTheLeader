package ftlsimulator.gui.content

import ftlsimulator.Simulation
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Priority.Always

object ViewContainer extends Pane {

  val simulationPane = new SimulationPane
  var editorPane = new EditorPane

  prefWidth = Simulation.simulationWidth
  prefHeight = Simulation.simulationHeight
  children = simulationPane

  this.width.onChange {
    Simulation.simulationWidth = this.width()
    simulationPane.clipRectangle.width = this.width()
    editorPane.clipRectangle.width = this.width()
    simulationPane.prefWidth = this.width()
    editorPane.prefWidth = this.width()
  }
  this.height.onChange {
    Simulation.simulationHeight = this.height()
    simulationPane.clipRectangle.height = this.height()
    editorPane.clipRectangle.height = this.height()
    simulationPane.prefHeight = this.height()
    editorPane.prefWidth = this.width()
  }

  hgrow = Always
  vgrow = Always


  def createNewEditorPane() = {
    editorPane = new EditorPane
  }

}
