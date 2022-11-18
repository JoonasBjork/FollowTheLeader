package ftlsimulator.gui.settings.sidebar.tabs

import ftlsimulator.Simulation
import ftlsimulator.gui.SimulationGui.timeline
import ftlsimulator.gui.content.ViewContainer
import ftlsimulator.gui.content.ViewContainer.{editorPane, simulationPane}
import ftlsimulator.gui.settings.sidebar.{EditorTabPane, SettingsPane}
import scalafx.Includes._
import scalafx.animation.Animation.Status
import scalafx.application.Platform
import scalafx.geometry.Insets
import scalafx.scene.control.{Button, Label, Slider, Tab, TextField}
import scalafx.scene.input.KeyCode.Enter
import scalafx.scene.input.KeyEvent
import scalafx.scene.layout.{HBox, VBox}

import scala.util.Try

class SimulationTab extends Tab {

  text = "Simulation options"
  closable = false


  //All fo the buttons, textfields, sliders and labels of this tab.
  private val fpsButton = new Button() {
    text = "Set simulation target fps"
    disable = true
    onAction = () => {
      updateFps()
      resetFpsTextField()
    }
  }


  private val fpsTextField: TextField = new TextField() {
    maxWidth = 55
    promptText = f"${Simulation.simulationFps}%1.0f".replace(",", ".")
    text.onChange {
      fpsButton.disable = text.value.isEmpty
      text = text.value.take(3).filter(c => (48 <= c && c <= 57))
    }
    onKeyPressed = (e: KeyEvent) => {
      if (e.code == Enter) {
        updateFps()
        resetFpsTextField()
      }
    }
  }


  private val fpsHBox = new HBox(10, fpsTextField, fpsButton)


  private val pauseButton = new Button() {
    text = "Pause simulation"
    onAction = () => {
      if (timeline.status() == Status.Running.delegate) {
        pauseSimulation()
      } else {
        unpauseSimulation()
      }
    }
  }


  private val rateButton = new Button {
    text = "Set simulation rate"
    disable = true
    onAction = () => {
      updateRate()
      resetRateTextField()
    }
  }

  private val rateTextField: TextField = new TextField() {
    maxWidth = 55
    promptText = f"${Simulation.simulationRate}%1.2f".replace(",", ".")
    text.onChange {
      rateButton.disable = text.value.isEmpty
      text = text.value.take(4).filter(c => (48 <= c && c <= 57) || c == 46)
    }
    onKeyPressed = (e: KeyEvent) => {
      if (e.code == Enter) {
        updateRate()
        resetRateTextField()
      }
    }
  }

  private val rateHBox = new HBox(10, rateTextField, rateButton)


  private val followerCountButton = new Button() {
    text = "Set number of followers"
    disable = true
    onAction = () => {
      updateFollowerCount()
      resetFollowerCountTextField()
    }
  }

  val followerCountTextField: TextField = new TextField() {
    maxWidth = 55
    promptText = Simulation.numberOfFollowers.toString
    text.onChange {
      followerCountButton.disable = text.value.isEmpty
      text = text.value.take(4).filter(c => (48 <= c && c <= 57))
    }
    onKeyPressed = (e: KeyEvent) => {
      if (e.code == Enter) {
        updateFollowerCount()
        resetFollowerCountTextField()
      }
    }
  }


  private val followerCountHBox = new HBox(10, followerCountTextField, followerCountButton)


  private val velocityArrowSlider = new Slider(Simulation.minVelocityArrowLength, Simulation.maxVelocityArrowLength, Simulation.velocityArrowLength) {
    blockIncrement = 1
    onMouseReleased = () => {
      ViewContainer.simulationPane.drawFollowerNodes()
    }
  }

  private val velocityArrowLabel = new Label() {
    text = s"Velocity arrow length multiplier ${math.round(velocityArrowSlider.value()).toInt}"
  }

  velocityArrowSlider.value.onChange {
    velocityArrowLabel.text = s"Velocity arrow length multiplier ${math.round(velocityArrowSlider.value()).toInt}"
    Simulation.velocityArrowLength = math.round(velocityArrowSlider.value()).toInt

  }


  private val forceArrowSlider = new Slider(Simulation.minForceArrowLength, Simulation.maxForceArrowLength, Simulation.forceArrowLength) {
    blockIncrement = 1
    onMouseReleased = () => {
      ViewContainer.simulationPane.drawFollowerNodes()
    }
  }

  private val forceArrowLabel = new Label() {
    text = s"Force arrow length multiplier ${math.round(forceArrowSlider.value()).toInt}"
  }

  forceArrowSlider.value.onChange {
    forceArrowLabel.text = s"Force arrow length multiplier ${math.round(forceArrowSlider.value()).toInt}"
    Simulation.forceArrowLength = math.round(forceArrowSlider.value()).toInt
    ViewContainer.simulationPane.drawFollowerNodes()
  }

  private val enableSectorForceButton = new Button() {
    text = "Enable sector force"
    onAction = () => {
      if (!Simulation.sectorForceEnabled()) {
        text = "Disable sector force"
        Simulation.sectorForceEnabled() = true
      } else {
        text = "Enable sector force"
        Simulation.sectorForceEnabled() = false
      }
      ViewContainer.simulationPane.drawFollowerNodes()
    }
  }

  private val sectorWidthSlider = new Slider(Simulation.minSectorWidth, Simulation.maxSectorWidth, Simulation.sectorWidth) {
    blockIncrement = 1
  }

  private val sectorWidthLabel = new Label() {
    text = "Sector area width " + f"${sectorWidthSlider.value()}%1.0f"
  }

  sectorWidthSlider.value.onChange {
    sectorWidthLabel.text = "Sector area width " + f"${sectorWidthSlider.value()}%1.0f"
    Simulation.sectorWidth = sectorWidthSlider.value().round.toInt
  }


  private val sectorRadiusSlider = new Slider(Simulation.minSectorRadiusMultiplier, Simulation.maxSectorRadiusMultiplier, Simulation.sectorRadiusMultiplier) {
    blockIncrement = 1
  }

  private val sectorRadiusLabel = new Label() {
    text = "Sector force radius " + f"${sectorRadiusSlider.value()}%1.0f"
  }

  sectorRadiusSlider.value.onChange {
    sectorRadiusLabel.text = "Sector force radius " + f"${sectorRadiusSlider.value()}%1.0f"
    Simulation.sectorRadiusMultiplier = sectorRadiusSlider.value().round.toInt
  }



  private val gotoEditorButton: Button = new Button {
    text = "Editor"
    onAction = () => {
      gotoEditor()
    }
  }

  private val simulationVBox = new VBox(20,
    pauseButton,
    fpsHBox,
    rateHBox,
    followerCountHBox,
    velocityArrowLabel,
    velocityArrowSlider,
    forceArrowLabel,
    forceArrowSlider,
    enableSectorForceButton,
    sectorWidthLabel,
    sectorWidthSlider,
    sectorRadiusLabel,
    sectorRadiusSlider,
    gotoEditorButton) {
    padding = Insets(10, 10, 10, 10)
  }

  content = simulationVBox


  //Methods to reduce repeating code for different elements in the GUI. Reset the values of the textfields after doing something.
  def resetFpsTextField() = {
    fpsTextField.promptText = f"${Simulation.simulationFps}%1.0f".replace(",", ".")
    fpsTextField.text = ""
  }

  def resetRateTextField() = {
    rateTextField.promptText = f"${Simulation.simulationRate}%1.2f".replace(",", ".")
    rateTextField.text = ""
  }

  def resetFollowerCountTextField() = {
    followerCountTextField.promptText = Simulation.numberOfFollowers.toString
    followerCountTextField.text = ""
  }

  //The actual functionalities of the GUI elements.

  //Pauses and unpauses the simulation
  def pauseSimulation(): Unit = {
    timeline.stop()
    pauseButton.text = "Unpause simulation"
  }

  def unpauseSimulation(): Unit = {
    timeline.play()
    pauseButton.text = "Pause simulation"
  }

  //Sets a new framerate to the simulation
  def updateFps() = {
    Simulation.simulationFps = Try(math.min(Simulation.maxFPS, math.max(Simulation.minFPS, fpsTextField.text.value.toDouble))).getOrElse(Simulation.simulationFps)
    if (timeline.status() == Status.Running.delegate) {
      timeline.stop()
      timeline.rate = Simulation.simulationFps / 40
      timeline.play()
    } else {
      timeline.rate = Simulation.simulationFps / 40
    }

    Platform.runLater(simulationPane.requestFocus())
  }

  //Sets a new rate to the simulation
  def updateRate() = {
    Simulation.simulationRate = Try(math.min(Simulation.maxRate, math.max(Simulation.minRate, rateTextField.text.value.toDouble))).getOrElse(Simulation.simulationRate)
    Platform.runLater(simulationPane.requestFocus())
  }

  //Changes the number of followers in the simulation
  def updateFollowerCount() = {
    Simulation.numberOfFollowers = Try(math.min(Simulation.maxFollowers, math.max(Simulation.minFollowers, followerCountTextField.text.value.toInt))).getOrElse(Simulation.numberOfFollowers)
    simulationPane.updateFollowerCount()
    simulationPane.resetPaneChildren()
    resetFollowerCountTextField()
    Platform.runLater(simulationPane.requestFocus())
  }

  //Pauses the simulation and changes the current ViewContainer to show the editor as well as the SettingsPane to show the editor settings
  def gotoEditor() = {
    pauseSimulation()
    Simulation.inSimulation() = false
    ViewContainer.children = editorPane
    Simulation.clearEditorSettings()
    EditorTabPane.createNewChildren()
    SettingsPane.children = EditorTabPane
  }


}
