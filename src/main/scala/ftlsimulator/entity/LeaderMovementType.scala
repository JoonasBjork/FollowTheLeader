package ftlsimulator.entity

import ftlsimulator.Simulation
import ftlsimulator.Simulation.{leader, random}
import ftlsimulator.gui.Keypress
import ftlsimulator.util.VectorVariable
import scalafx.scene.input.KeyCode

sealed trait LeaderMovementType {
  def getForce: VectorVariable
}

object Wander extends LeaderMovementType {

  var lastRandom = (0.0, 0.0)
  //Controls how quickly the leader is able to turn
  val randomMax = 2
  //Controls how different the returned force can be from the current velocity.
  val variance = 10

  def limit(value: Double, min: Double, max: Double): Double =
    math.min(math.max(value, min), max)

  //Returns a force that doesn't deviate from the previous velocity too much.
  def getForce = {
    lastRandom =
      (limit(lastRandom._1 + (random.nextDouble() - 0.5), -randomMax, randomMax),
      limit(lastRandom._2 + (random.nextDouble() - 0.5), -randomMax, randomMax))
    val nextDir = (leader.velocity.x + (lastRandom._1) * variance, leader.velocity.y + (lastRandom._2) * variance)
    (leader.currentForces.head addxandy(nextDir._1, nextDir._2)) withMagnitude (0.2)
  }
}

object Seek extends LeaderMovementType {
  //Returns the force that propels the leader towards its current goal.
  def getForce = {
    val desiredDir = leader.goal sub leader.location
    val speedTowardsGoal = if (leader.location.distanceSquared(leader.goal) < Simulation.settings.lArriveRange * Simulation.settings.lArriveRange) {
      Simulation.settings.lMaxSpeed * (leader.location.distance(leader.goal) / Simulation.settings.lArriveRange)
    } else {
      Simulation.settings.lMaxSpeed
    }
    val seekForce = desiredDir withMagnitude (speedTowardsGoal) sub leader.velocity
    seekForce mul 0.05
  }
}


object UserControl extends LeaderMovementType {
  def getForce = {
    val minVelSquaredToTurn = if (Simulation.settings.lMaxSpeed * Simulation.settings.lMaxSpeed < 0.1) Simulation.settings.lMaxSpeed * Simulation.settings.lMaxSpeed * 0.5 else 0.1
    //Takes the user input and returns a force according to it.
    val desiredDir = if (Keypress.keysPressed.isEmpty) {
      VectorVariable()
    } else {
      Keypress.keysPressed.map({
        case KeyCode.W =>
          leader.orientation
        case KeyCode.A =>
          if (leader.velocity.magnitudeSquared() > minVelSquaredToTurn) {
            leader.orientation.turnLeft()
          } else {
            VectorVariable()
          }
        case KeyCode.S =>
          if (leader.velocity.magnitudeSquared() > minVelSquaredToTurn)
            leader.orientation.turnOpposite()
          else
            VectorVariable()
        case KeyCode.D =>
          if (leader.velocity.magnitudeSquared() > minVelSquaredToTurn) {
            leader.orientation.turnRight()
          } else {
            VectorVariable()
          }
        case _ => VectorVariable()
      }).reduce(_ add _)
    }
    leader.velocity = leader.velocity mul 0.99
    val ucForce = desiredDir.withMagnitude(Simulation.settings.lSteeringForce / 5)
    ucForce
  }
}

