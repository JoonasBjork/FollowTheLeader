package ftlsimulator.gui

import ftlsimulator.Simulation
import scalafx.application.JFXApp
import scalafx.application.JFXApp.PrimaryStage
import scalafx.scene.Scene

object Main extends JFXApp {

  stage = new PrimaryStage {
    title = "SimulationGui"
    minWidth = Simulation.minSimulationWidth
    minHeight = Simulation.minSimulationHeight
    scene = new Scene() {
      stylesheets.add("dark.css")
      Keypress.handleInput(this)
      root = SimulationGui.rootPane
    }
  }

  SimulationGui.timeline.play()

}
