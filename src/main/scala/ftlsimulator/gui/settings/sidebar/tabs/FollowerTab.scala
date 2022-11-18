package ftlsimulator.gui.settings.sidebar.tabs

import ftlsimulator.{Settings, Simulation}
import scalafx.geometry.Insets
import scalafx.scene.control.{Label, Slider, Tab}
import scalafx.scene.layout.VBox

class FollowerTab(s: Settings) extends Tab {

  text = "Follower options"
  closable = false

  //Slider to change the followers' max speed
  val maxSpeedSlider = new Slider(Simulation.minSpeed, Simulation.maxSpeed, s.fMaxSpeed) {
    blockIncrement = 1
  }
  private val maxSpeedLabel = new Label() {
    text = "Follower's max speed " + f"${maxSpeedSlider.value()}%1.1f"
  }

  maxSpeedSlider.value.onChange {
    maxSpeedLabel.text = "Follower's max speed " + f"${maxSpeedSlider.value()}%1.1f"
    s.fMaxSpeed = maxSpeedSlider.value()
  }


  //Slider to change the followers' steeringForce
  val steeringForceSlider = new Slider(Simulation.minForce, Simulation.maxForce, s.fSteeringForce) {
    blockIncrement = 1
  }
  private val steeringForceLabel = new Label() {
    text = "Follower's steering force " + f"${steeringForceSlider.value()}%1.1f"
  }

  steeringForceSlider.value.onChange {
    steeringForceLabel.text = "Follower's steering force " + f"${steeringForceSlider.value()}%1.1f"
    s.fSteeringForce = steeringForceSlider.value()
  }


  //Slider to change the followers' nearby range
  val nearbyRangeSlider = new Slider(Simulation.minNearbyRange, Simulation.maxNearbyRange, s.fNearbyRange) {
    blockIncrement = 1
  }

  private val nearbyRangeLabel = new Label() {
    text = s"Follower's vision range ${math.round(nearbyRangeSlider.value()).toInt}"
  }

  nearbyRangeSlider.value.onChange {
    nearbyRangeLabel.text = s"Follower's vision range ${math.round(nearbyRangeSlider.value()).toInt}"
    s.fNearbyRange = math.round(nearbyRangeSlider.value()).toInt
  }


  //Slider to change the followers' arrive range
  val arriveRangeSlider = new Slider(10, Simulation.maxArriveRange, s.fArriveRange) {
    blockIncrement = 1
  }

  private val arriveRangeLabel = new Label() {
    text = s"Follower's arrival range ${math.round(arriveRangeSlider.value()).toInt}"
  }

  arriveRangeSlider.value.onChange {
    arriveRangeLabel.text = s"Follower's arrival range ${math.round(arriveRangeSlider.value()).toInt}"
    s.fArriveRange = math.round(arriveRangeSlider.value()).toInt
  }


  private val followerVBox = new VBox(20,
    maxSpeedLabel,
    maxSpeedSlider,
    steeringForceLabel,
    steeringForceSlider,
    nearbyRangeLabel,
    nearbyRangeSlider,
    arriveRangeLabel,
    arriveRangeSlider) {
    padding = Insets(10, 10, 10, 10)
  }


  content = followerVBox

}
