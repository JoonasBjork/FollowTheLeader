package ftlsimulator.entity

import ftlsimulator.Simulation
import ftlsimulator.util.VectorVariable

import scala.collection.mutable.Buffer

class Leader(xPos: Double, yPos: Double) extends Individual {

  //basic variables of the leader
  var orientation = new VectorVariable(1, 0)
  var velocity = VectorVariable()
  var location = new VectorVariable(xPos, yPos)
  var goal: VectorVariable = new VectorVariable(500, 500)

  val indexInLine = 0

  var currentForces: Buffer[VectorVariable] = Buffer.fill(2)(VectorVariable())
  var currentMovementType: LeaderMovementType = Wander

  def updateForces() = {
    currentForces(0) = currentMovementType.getForce
    currentForces(1) = obAvoidForce()
  }


  //The TopLeft and TopRight corner are the corners of the triangle. The BottomLeft and BottomRight corners
  //Are for ease of calculations. These are used by the followers to check if they are in the triangle.
  private var sectorRadius = Simulation.sectorRadiusMultiplier * velocity.magnitude()
  var rectangleBLCorner = velocity.turnLeft() muly (-1) withMagnitude (Simulation.sectorWidth / 2)
  var rectangleBRCorner = velocity.turnRight() muly (-1) withMagnitude (Simulation.sectorWidth / 2)
  var rectangleTLCorner = velocity muly (-1) withMagnitude (sectorRadius) add rectangleBLCorner
  var rectangleTRCorner = velocity muly (-1) withMagnitude (sectorRadius) add rectangleBRCorner

  def updateSector() = {
    sectorRadius = Simulation.sectorRadiusMultiplier * velocity.magnitude()
    rectangleBLCorner = velocity.turnLeft() muly (-1) withMagnitude (Simulation.sectorWidth / 2)
    rectangleBRCorner = velocity.turnRight() muly (-1) withMagnitude (Simulation.sectorWidth / 2)
    rectangleTLCorner = velocity muly (-1) withMagnitude (sectorRadius) add rectangleBLCorner
    rectangleTRCorner = velocity muly (-1) withMagnitude (sectorRadius) add rectangleBRCorner
  }


  //Moves away from the walls (Obstacle avoidance)
  def obAvoidForce() = {

    var obAvoidForce = new VectorVariable(0, 0)

    val distx1 = this.location.x
    val distx2 = Simulation.simulationWidth - this.location.x
    val disty1 = this.location.y
    val disty2 = Simulation.simulationHeight - this.location.y

    if (distx1 < Simulation.settings.lWallAvoidRange) obAvoidForce = obAvoidForce add new VectorVariable(1 / distx1, 0)
    if (distx2 < Simulation.settings.lWallAvoidRange) obAvoidForce = obAvoidForce add new VectorVariable(-1 / distx2, 0)
    if (disty1 < Simulation.settings.lWallAvoidRange) obAvoidForce = obAvoidForce add new VectorVariable(0, 1 / disty1)
    if (disty2 < Simulation.settings.lWallAvoidRange) obAvoidForce = obAvoidForce add new VectorVariable(0, -1 / disty2)

    obAvoidForce
  }


  //Updates the leader's location as well as its velocity
  def move() = {

    updateForces()
    updateSector()

    var totalForce = VectorVariable()

    //Force depending on the current movement type
    totalForce = totalForce add currentForces.head

    //Moves away from the walls (Obstacle avoidance)
    totalForce = totalForce add (currentForces(1) mul 20)


    totalForce = totalForce.limitMagnitude(Simulation.settings.lSteeringForce * Simulation.simulationRate)

    this.location = (this.location add (this.velocity withMagnitude (this.velocity.magnitude() * Simulation.simulationRate)))
      .limitXToRange(0.1, Simulation.simulationWidth - 0.1)
      .limitYToRange(0.1, Simulation.simulationHeight - 0.1)
    this.velocity = (this.velocity add totalForce).limitMagnitude(Simulation.settings.lMaxSpeed)


    //Keep track of the orientation while standing still.
    if (this.velocity.x != 0 && this.velocity.y != 0) this.orientation = this.velocity withMagnitude (1)

  }


  override def toString = s"Leader(${this.location.x}, ${this.location.y})"


}
