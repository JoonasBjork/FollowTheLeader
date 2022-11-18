package ftlsimulator.entity

import ftlsimulator.Simulation
import ftlsimulator.util.VectorVariable


sealed trait FollowerMovementType {
  def getForce: VectorVariable
}

//Follows the leader
class FollowTheLeader(f: Follower) extends FollowerMovementType {
  def getForce = {
    val desiredDir = (Simulation.leader.location sub (Simulation.leader.velocity mul 2)) sub f.location
    val speedTowardsLeader = if (f.location.distanceSquared(Simulation.leader.location) < Simulation.settings.fArriveRange * Simulation.settings.fArriveRange) {
      Simulation.settings.fMaxSpeed * f.location.distance(Simulation.leader.location) / (Simulation.settings.fArriveRange)
    } else {
      Simulation.settings.fMaxSpeed
    }
    val ftlForce = desiredDir.withMagnitude(speedTowardsLeader) sub f.velocity
    ftlForce
  }
}

//Follows the follower with the previous index. The first follower follows the leader
class FollowPrevious(f: Follower) extends FollowerMovementType {
  def getForce = {
    val desiredDir = f.previousInLine.location sub (f.previousInLine.velocity mul 2) sub f.location
    val speedTowardsNext = if (f.location.distanceSquared(f.previousInLine.location) < Simulation.settings.fArriveRange * Simulation.settings.fArriveRange) {
      Simulation.settings.fMaxSpeed * f.location.distance(f.previousInLine.location) / (Simulation.settings.fArriveRange)
    } else {
      Simulation.settings.fMaxSpeed
    }
    val followNextForce = desiredDir.withMagnitude(speedTowardsNext) sub f.velocity
    followNextForce
  }
}

//Companion objects for the classes to store which movement type the followers have in Simulation.
sealed trait SelectedFollowerMovementType

case object FollowTheLeader extends SelectedFollowerMovementType

case object FollowPrevious extends SelectedFollowerMovementType

