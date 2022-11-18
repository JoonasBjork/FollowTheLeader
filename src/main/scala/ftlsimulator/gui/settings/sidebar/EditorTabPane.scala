package ftlsimulator.gui.settings.sidebar

import ftlsimulator.Simulation
import ftlsimulator.gui.settings.sidebar.tabs.{EditorTab, FollowerTab, LeaderTab}
import scalafx.scene.control.TabPane
import scalafx.scene.layout.Priority.Always

object EditorTabPane extends TabPane {

  minWidth = 300
  maxWidth = 300
  vgrow = Always

  private var editorFollowerTab = new FollowerTab(Simulation.editorSettings)
  private var editorLeaderTab = new LeaderTab(Simulation.editorSettings)
  private var editorTab = new EditorTab

  tabs = List(editorTab, editorLeaderTab, editorFollowerTab)

  //Used to update all of the sliders and input fields in the tabs.
  def updateAllEditorTabs() = {
    editorLeaderTab.steeringForceSlider.value = Simulation.editorSettings.lSteeringForce
    editorLeaderTab.maxSpeedSlider.value = Simulation.editorSettings.lMaxSpeed
    editorLeaderTab.wallAvoidSlider.value = Simulation.editorSettings.lWallAvoidRange
    editorLeaderTab.arriveRangeSlider.value = Simulation.editorSettings.lArriveRange
    editorFollowerTab.steeringForceSlider.value = Simulation.editorSettings.fSteeringForce
    editorFollowerTab.maxSpeedSlider.value = Simulation.editorSettings.fMaxSpeed
    editorFollowerTab.nearbyRangeSlider.value = Simulation.editorSettings.fNearbyRange
    editorFollowerTab.arriveRangeSlider.value = Simulation.editorSettings.fArriveRange
  }

  //Used when opening the editor to have default values in everything
  def createNewChildren() = {
    editorFollowerTab = new FollowerTab(Simulation.editorSettings)
    editorLeaderTab = new LeaderTab(Simulation.editorSettings)
    editorTab = new EditorTab
    tabs = List(editorTab, editorLeaderTab, editorFollowerTab)
  }

}
