package ftlsimulator.gui.settings.sidebar

import scalafx.scene.layout.Pane
import scalafx.scene.layout.Priority.Always

//Contains the simulationTabPane or the EditorTabPane depending if the program is in the simulation or in the editor.
object SettingsPane extends Pane {
  minWidth = 300
  maxWidth = 300
  vgrow = Always
  children = SimulationTabPane
  styleClass = List("settings-pane")
}
