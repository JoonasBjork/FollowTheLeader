package ftlsimulator.entity

import ftlsimulator.Simulation
import ftlsimulator.Simulation.leader
import ftlsimulator.util.VectorVariable

import scala.collection.mutable.Buffer

class Follower(xPos: Double, yPos: Double, xVel: Double, yVel: Double, index: Int) extends Individual {


  //basic variables of the follower
  var velocity = new VectorVariable(xVel, yVel) limitMagnitude (Simulation.settings.fMaxSpeed)
  var location = new VectorVariable(xPos, yPos)

  var currentMovementType: FollowerMovementType =
    Simulation.followerMovementType match {
      case FollowTheLeader => new FollowTheLeader(this)
      case FollowPrevious => new FollowPrevious(this)
    }


  //Buffer that contains all of the forces that currently affect this follower
  var currentForces: Buffer[VectorVariable] = Buffer.fill(3)(VectorVariable())


  //This follower's position in all of the individuals
  val indexInLine = index

  //The individual with the previous index
  val previousInLine = {
    if (indexInLine == 0)
      leader
    else
      Simulation.followers(indexInLine - 1)
  }
  //Used for checking if the follower is inside the sector in front of the leader.
  private var outOfSector = true


  //Finds the other followers in a circle area around this one
  def findAllInRange(radius: Double): Buffer[Follower] = {
    for {
      follower <- Simulation.followers
      if (this.location.distanceSquared(follower.location) < radius * radius && follower != this)
    } yield follower
  }

  //Updates and stores the forces that the individual is affected by
  def updateForces() = {
    currentForces(2) = sectorAvoidForce()
    if (outOfSector)
      currentForces(0) = currentMovementType.getForce
    else
      currentForces(0) = VectorVariable()
    currentForces(1) = separationForce()
    outOfSector = true
  }


  //Avoids hitting other individuals (Separate)
  def separationForce() = {
    val avoidForces = for (follower <- findAllInRange(Simulation.settings.fNearbyRange)) yield {
      val oppositeDirection = (follower.location sub this.location)
      val speedAway = -20 / this.location.distance(follower.location)
      oppositeDirection.withMagnitude(speedAway)
    }

    //Separates from the followers
    var separationForce = if (avoidForces.nonEmpty) {
      avoidForces.reduce(_ add _)
    } else {
      VectorVariable()
    }

    //Separates from the leader
    separationForce = separationForce add (leader.location sub this.location withMagnitude ((-200 / (this.location.distance(leader.location)))))

    separationForce
  }

  def sectorAvoidForce(): VectorVariable = {
    //Point relative to the leader
    if (Simulation.sectorForceEnabled()) {
      val relativePosition = VectorVariable.pointRelativeTo(leader.location, this.location)

      //Checks if the follower is inside the triangle
      if (VectorVariable.insideTriangle(relativePosition, VectorVariable(), leader.rectangleTRCorner, leader.rectangleTLCorner)) {
        //Check if the follower is in the left or in the right triangle by checking if it is in the left square
        //Works by checking if the point is in the area "on top of" the left side of the triangle
        val OB = VectorVariable.pointToPoint(VectorVariable(), leader.rectangleBLCorner)
        val OM = VectorVariable.pointToPoint(VectorVariable(), relativePosition)
        val dotOBOM = OB.dotProduct(OM)
        val dotOBOB = OB.dotProduct(OB)
        if (0 <= dotOBOM && dotOBOM <= dotOBOB) {
          outOfSector = false
          leader.velocity.turnLeft().withMagnitude(100 / (this.location.distance(leader.location)))
        } else {
          outOfSector = false
          leader.velocity.turnRight().withMagnitude(100 / (this.location.distance(leader.location)))
        }
      }
      else
        VectorVariable()
    }
    else
      VectorVariable()
  }

  //Tries to follow the leader and avoid other individuals.
  def move() = {
    updateForces()
    var totalForce = VectorVariable()

    //Current movement type
    totalForce = totalForce add currentForces.head

    //Separates from others (Separate)
    totalForce = totalForce add currentForces(1)

    //Avoids getting in a sector in front of the leader.
    totalForce = totalForce add currentForces(2)

    //Calculate the force that affects the follower
    totalForce = totalForce.limitMagnitude(Simulation.settings.fSteeringForce * Simulation.simulationRate)

    //Updates the follower's location and calculates the new velocity
    this.location = (this.location add (this.velocity withMagnitude (velocity.magnitude() * Simulation.simulationRate)))
      .limitXToRange(- Simulation.settings.fNearbyRange, Simulation.simulationWidth + Simulation.settings.fNearbyRange)
      .limitYToRange(- Simulation.settings.fNearbyRange, Simulation.simulationHeight + Simulation.settings.fNearbyRange)
    this.velocity = (this.velocity add totalForce).limitMagnitude(Simulation.settings.fMaxSpeed)


  }


  override def toString = s"Follower(${this.location.x}, ${this.location.y})"


}
