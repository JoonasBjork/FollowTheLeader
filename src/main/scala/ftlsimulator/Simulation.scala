package ftlsimulator

import ftlsimulator.entity.{FollowTheLeader, Follower, Leader, SelectedFollowerMovementType}
import ftlsimulator.util.VectorVariable
import scalafx.beans.property.BooleanProperty

import scala.collection.mutable.Buffer
import scala.util.Random

object Simulation {

  val random = new Random()

  val leader = new Leader(200, 200)
  val followers = Buffer[Follower]()

  //Adjustable settings of the simulation that are stored in a file
  var settings = new Settings
  //The settings of the editor
  var editorSettings = new Settings

  //Adjustable settings that aren't stored in a file
  var numberOfFollowers = 5
  var simulationFps = 40.0
  var simulationRate = 1.0
  var simulationHeight: Double = 1000
  var simulationWidth: Double = 1000
  var followerMovementType: SelectedFollowerMovementType = FollowTheLeader
  var sectorWidth = 220
  var sectorRadiusMultiplier = 20

  //Limits to the adjustable parameters of the simulation
  val minSimulationHeight = 720
  val minSimulationWidth = 720
  val maxFollowers = 1000
  val minFollowers = 0
  val maxFPS = 300
  val minFPS = 10
  val maxRate = 1.0
  val minRate = 0.01
  val maxSectorWidth = 450
  val minSectorWidth = 20
  val maxSectorRadiusMultiplier = 80
  val minSectorRadiusMultiplier = 10


  //Limits to the adjustable parameters of the individuals
  val maxNearbyRange = 600
  val minNearbyRange = 10
  val maxArriveRange = 500
  val minArriveRange = 10
  val maxWallAvoidRange = 200
  val minWallAvoidRange = 0
  val maxSpeed = 10
  val minSpeed = 0.1
  val maxForce = 2.5
  val minForce = 0.1


  //Values used for drawing of the simulation
  var forceArrowLength = 5
  var velocityArrowLength = 10
  val leaderRadius = 10
  val followerRadius = 4

  //Limits for values used for drawing the simulation elements
  val maxForceArrowLength = 20
  val minForceArrowLength = 1
  val maxVelocityArrowLength = 20
  val minVelocityArrowLength = 1


  //Values needed for checking for different states of the simulation
  var showGoal = BooleanProperty(false)
  var showFollowerVelocityArrows = BooleanProperty(true)
  var showFollowerForceArrows = BooleanProperty(false)
  var sectorForceEnabled = BooleanProperty(false)
  //Variable to check if the application is in the simulator (true) or in the editor (false)
  var inSimulation = BooleanProperty(true)


  //Finds the number of threads on the machine and distributes the heavy calculations to those
  val threadCount = Runtime.getRuntime.availableProcessors()


  //Calls the individual's move() -methods. If the number of followers is over 50 distributes the calculations to multiple threads
  def update() = {
    leader.move()
    if (numberOfFollowers > 50) {
      val threads = for (i <- 0 until Simulation.threadCount) yield new Thread {
        override def run(): Unit = {
          for (j <- i * numberOfFollowers / Simulation.threadCount until (i + 1) * numberOfFollowers / Simulation.threadCount) {
            followers(j).move()
          }
        }
      }
      threads.foreach(_.start)
      threads.foreach(_.join)
    } else {
      followers.foreach(_.move())
    }
  }

  // Updates the information of the individuals
  // Used by the GUI to display changes faster
  def updateIndividualForces() = {
    leader.updateForces()
    leader.updateSector()
    followers.foreach(_.updateForces())
  }


  //Creates a new follower
  def addFollower(xPos: Double, yPos: Double, xVel: Double, yVel: Double): Unit = {
    followers += new Follower(xPos, yPos, xVel, yVel, followers.length)
  }

  //Changes the leader's position and velocity
  def updateLeader(xPos: Double, yPos: Double, xVel: Double, yVel: Double): Unit = {
    leader.location = new VectorVariable(xPos, yPos)
    leader.velocity = new VectorVariable(xVel, yVel) limitMagnitude (settings.fMaxSpeed)
    leader.currentForces = Buffer.fill(2)(VectorVariable())
  }

  def updateGoal(x: Double, y: Double): Unit = {
    leader.goal = new VectorVariable(x, y)
  }

  def clearEditorSettings() =
    editorSettings = new Settings

}
