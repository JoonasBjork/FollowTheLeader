package ftlsimulator.gui.settings.sidebar

import ftlsimulator.Simulation
import ftlsimulator.gui.settings.sidebar.tabs.{FollowerTab, LeaderTab, SimulationTab}
import scalafx.scene.control.TabPane

object SimulationTabPane extends TabPane {


  minWidth = 300
  maxWidth = 300

  private val followerTab = new FollowerTab(Simulation.settings)
  private val leaderTab = new LeaderTab(Simulation.settings)
  private val simulationTab = new SimulationTab


  //Used to update all of the sliders and input fields in the tabs.
  def updateAllSimulationTabs() = {
    simulationTab.followerCountTextField.promptText = Simulation.numberOfFollowers.toString
    leaderTab.steeringForceSlider.value = Simulation.settings.lSteeringForce
    leaderTab.maxSpeedSlider.value = Simulation.settings.lMaxSpeed
    leaderTab.wallAvoidSlider.value = Simulation.settings.lWallAvoidRange
    leaderTab.arriveRangeSlider.value = Simulation.settings.lArriveRange
    followerTab.steeringForceSlider.value = Simulation.settings.fSteeringForce
    followerTab.maxSpeedSlider.value = Simulation.settings.fMaxSpeed
    followerTab.nearbyRangeSlider.value = Simulation.settings.fNearbyRange
    followerTab.arriveRangeSlider.value = Simulation.settings.fArriveRange
  }

  tabs = List(simulationTab, leaderTab, followerTab)

}
