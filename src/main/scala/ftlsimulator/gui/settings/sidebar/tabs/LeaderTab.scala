package ftlsimulator.gui.settings.sidebar.tabs

import ftlsimulator.{Settings, Simulation}
import scalafx.geometry.Insets
import scalafx.scene.control.{Label, Slider, Tab}
import scalafx.scene.layout.VBox

class LeaderTab(s: Settings) extends Tab {
  text = "Leader options"
  closable = false


  //Slider to change the leader's steering force
  val steeringForceSlider = new Slider(Simulation.minForce, Simulation.maxForce, s.lSteeringForce) {
    blockIncrement = 1
  }
  private val steeringForceLabel = new Label() {
    text = "Leader's steering force " + f"${steeringForceSlider.value()}%1.1f"
  }

  steeringForceSlider.value.onChange {
    steeringForceLabel.text = "Leader's steering force " + f"${steeringForceSlider.value()}%1.1f"
    s.lSteeringForce = steeringForceSlider.value()
  }


  //Slider to change the leader's maximum speed
  val maxSpeedSlider = new Slider(Simulation.minSpeed, Simulation.maxSpeed, s.lMaxSpeed) {
    blockIncrement = 1
  }

  private val maxSpeedLabel = new Label() {
    text = "Leader's max speed " + f"${maxSpeedSlider.value()}%1.1f"
  }

  maxSpeedSlider.value.onChange {
    maxSpeedLabel.text = "Leader's max speed " + f"${maxSpeedSlider.value()}%1.1f"
    s.lMaxSpeed = maxSpeedSlider.value()
  }


  //Slider to change how far the from the walls the wall avoid force affects the leader
  val wallAvoidSlider = new Slider(Simulation.minWallAvoidRange, Simulation.maxWallAvoidRange, s.lWallAvoidRange) {
    blockIncrement = 1
  }

  private val wallAvoidLabel = new Label() {
    text = s"Leader's wall avoid range ${math.round(wallAvoidSlider.value()).toInt}"
  }

  wallAvoidSlider.value.onChange {
    wallAvoidLabel.text = s"Leader's wall avoid range ${math.round(wallAvoidSlider.value()).toInt}"
    s.lWallAvoidRange = math.round(wallAvoidSlider.value()).toInt
  }


  //Slider to change the leaders arrive range
  val arriveRangeSlider = new Slider(Simulation.minArriveRange, Simulation.maxArriveRange, s.lArriveRange) {
    blockIncrement = 1
  }

  private val arriveRangeLabel = new Label() {
    text = s"Leader's arrival range ${math.round(arriveRangeSlider.value()).toInt}"
  }

  arriveRangeSlider.value.onChange {
    arriveRangeLabel.text = s"Leader's arrival range ${math.round(arriveRangeSlider.value()).toInt}"
    s.lArriveRange = math.round(arriveRangeSlider.value()).toInt
  }


  private val leaderVBox = new VBox(20,
    maxSpeedLabel,
    maxSpeedSlider,
    steeringForceLabel,
    steeringForceSlider,
    wallAvoidLabel,
    wallAvoidSlider,
    arriveRangeLabel,
    arriveRangeSlider) {
    padding = Insets(10, 10, 10, 10)
  }

  content = leaderVBox


}
