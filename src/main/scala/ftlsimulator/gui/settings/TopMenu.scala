package ftlsimulator.gui.settings

import ftlsimulator.Simulation
import ftlsimulator.Simulation.{inSimulation, leader}
import ftlsimulator.entity.{FollowPrevious, FollowTheLeader, Seek, UserControl, Wander}
import ftlsimulator.file.FileReader
import ftlsimulator.gui.Main.stage
import ftlsimulator.gui.content.ViewContainer
import ftlsimulator.gui.content.ViewContainer.{editorPane, simulationPane}
import ftlsimulator.gui.settings.sidebar.{EditorTabPane, SimulationTabPane}
import scalafx.Includes._
import scalafx.scene.control.Alert.AlertType
import scalafx.scene.control.{Alert, Menu, MenuBar, MenuItem, RadioMenuItem, SeparatorMenuItem, ToggleGroup}
import scalafx.stage.FileChooser
import scalafx.stage.FileChooser.ExtensionFilter

object TopMenu extends MenuBar {


  // Opens the file explorer and lets the user choose a file. After choosing a file it is read in FileReader
  // If the read is successful updates the settings of the simulation through ViewContainer
  // Updates all of the GUI settings to their correct values through their respective TabPanes
  // If the file read is unsuccessful, gives the user an error message containing the error.
  private val openItem = new MenuItem("Open") {
    onAction = () => {
      val fileChooser = new FileChooser {
        extensionFilters.add(new ExtensionFilter("csv-file", "*.csv"))
      }
      val selectedFile = fileChooser.showOpenDialog(stage)
      if (selectedFile != null) {
        FileReader.readFile(selectedFile.toString)
        if (FileReader.readSucceeded()) {
          if (inSimulation()) {
            Simulation.settings.updateSettingsFromFile()
            simulationPane.updateIndividualsFromFile()
            simulationPane.resetPaneChildren()
            SimulationTabPane.updateAllSimulationTabs()
            Simulation.updateIndividualForces()
            simulationPane.drawFollowerNodes()
          } else {
            Simulation.editorSettings.updateSettingsFromFile()
            editorPane.updateIndividualsFromFile()
            EditorTabPane.updateAllEditorTabs()
          }
        } else {
          val errorAlert = new Alert(AlertType.Error, "File read unsuccessful")
          errorAlert.contentText = FileReader.errorMessage
          errorAlert.showAndWait()
        }
      }
    }
  }

  private val rerunItem = new MenuItem("Run file again") {
    disable <== FileReader.readSucceeded.not or inSimulation.not
    onAction = () => {
      Simulation.settings.updateSettingsFromFile()
      simulationPane.updateIndividualsFromFile()
      simulationPane.resetPaneChildren()
      SimulationTabPane.updateAllSimulationTabs()
      Simulation.updateIndividualForces()
      simulationPane.drawFollowerNodes()
    }
  }

  // Only available in the editor mode
  // Opens the file explorer to find a directory.
  // Saves the file in editorPane.
  private val saveItem = new MenuItem("Save") {
    disable <== inSimulation
    onAction = () => {
      val fileChooser = new FileChooser {
        extensionFilters.add(new ExtensionFilter("CSV files", "*.csv"))
      }
      val file = fileChooser.showSaveDialog(stage)
      if (file != null) {
        editorPane.saveToFile(file)
      }
    }
  }

  //Quits the simulation
  private val exitItem = new MenuItem("Exit") {
    onAction = () => sys.exit(0)
  }

  //Menubar menu general options
  private val fileMenu = new Menu("File") {
    items = List(openItem, rerunItem, saveItem, new SeparatorMenuItem, exitItem)
  }


  //Leader movement type
  private val wanderTypeItem = new RadioMenuItem("Wander") {
    selected = true
    onAction = () => {
      leader.currentMovementType = Wander
      Simulation.updateIndividualForces()
      ViewContainer.simulationPane.updatePositions()
    }
  }

  //Changes the leader's movement type
  private val seekTypeItem = new RadioMenuItem("Seek") {
    selected <==> Simulation.showGoal
    onAction = () => {
      leader.currentMovementType = Seek
      Simulation.updateIndividualForces()
      ViewContainer.simulationPane.updatePositions()
    }
  }

  //Changes the leader's movement type
  private val ucTypeItem = new RadioMenuItem("User control") {
    onAction = () => {
      leader.currentMovementType = UserControl
      Simulation.updateIndividualForces()
      ViewContainer.simulationPane.updatePositions()
    }
  }

  private val leaderMovementTypeToggleGroup = new ToggleGroup {
    toggles = List(wanderTypeItem, seekTypeItem, ucTypeItem)
  }

  //Menubar menu for toggling the leader's movement type
  private val leaderMovementTypeMenu = new Menu("Leader movement type") {
    items = List(wanderTypeItem, seekTypeItem, ucTypeItem)
  }

  //Changes the followers' movement type
  private val followLeaderItem = new RadioMenuItem("Follow the leader") {
    selected = true
    onAction = () => {
      Simulation.followerMovementType = FollowTheLeader
      Simulation.followers.foreach(f => f.currentMovementType = new FollowTheLeader(f))
      Simulation.updateIndividualForces()
      ViewContainer.simulationPane.drawFollowerNodes()
    }
  }

  //Changes the followers' movement type
  private val followPreviousItem = new RadioMenuItem("Follow previous") {
    onAction = () => {
      Simulation.followerMovementType = FollowPrevious
      Simulation.followers.foreach(f => f.currentMovementType = new FollowPrevious(f))
      Simulation.updateIndividualForces()
      ViewContainer.simulationPane.drawFollowerNodes()
    }
  }

  private val followerMovementTypeToggleGroup = new ToggleGroup {
    toggles = List(followLeaderItem, followPreviousItem)
  }

  //Menu for toggling the followers' movement type
  private val followerMovementTypeMenu = new Menu("Follower movement type") {
    items = List(followLeaderItem, followPreviousItem)
  }


  //Sets the followers' velocity arrows to be visible or invisible
  private val followerVelocityArrowsItem = new RadioMenuItem("Toggle velocity arrows") {
    selected <==> Simulation.showFollowerVelocityArrows
    onAction = () => {
      Simulation.updateIndividualForces()
      simulationPane.drawFollowerNodes()
    }
  }

  //Sets the followers' force arrows to be visible or invisible
  private val followerForceArrowsItem = new RadioMenuItem("Toggle force arrows") {
    selected <==> Simulation.showFollowerForceArrows
    onAction = () => {
      Simulation.updateIndividualForces()
      simulationPane.drawFollowerNodes()
    }
  }

  //Menu for toggling the followers' arrows
  private val followersMenu = new Menu("Arrows") {
    items = List(followerVelocityArrowsItem, followerForceArrowsItem)
  }

  menus = List(fileMenu, leaderMovementTypeMenu, followerMovementTypeMenu, followersMenu)
}
