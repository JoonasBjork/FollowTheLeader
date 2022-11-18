package ftlsimulator.gui

import ftlsimulator.Simulation
import ftlsimulator.gui.content.ViewContainer
import ftlsimulator.gui.settings.TopMenu
import ftlsimulator.gui.settings.sidebar.SettingsPane
import org.scalafx.extras.offFXAndWait
import scalafx.animation.{Animation, KeyFrame, Timeline}
import scalafx.scene.layout.GridPane
import scalafx.util.Duration


object SimulationGui {


  //Updates the simulation
  val timeline = Timeline(Seq(KeyFrame(Duration(1000 / Simulation.simulationFps), "", (event) => {

    //Creates a new thread where it does all the heavy calculations. Waits until all of the threads are ready to update the GUI.
    offFXAndWait {
      Simulation.update()
    }
    ViewContainer.simulationPane.updatePositions()

  })))

  timeline.cycleCount = Animation.Indefinite

  val rootPane = new GridPane()
  rootPane.add(TopMenu, 0, 0, 3, 1)
  rootPane.add(ViewContainer, 0, 1)
  rootPane.add(SettingsPane, 1, 1)

}

