package ftlsimulator.gui.content

import ftlsimulator.Simulation
import ftlsimulator.Simulation.leader
import ftlsimulator.entity.Seek
import ftlsimulator.file.FileReader
import javafx.scene.{shape => jfxss}
import scalafx.Includes._
import scalafx.application.Platform
import scalafx.scene.Group
import scalafx.scene.input.MouseEvent
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Priority.Always
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Line, Rectangle}

class SimulationPane extends Pane {


  //Parameters of the pane
  val clipRectangle = Rectangle(Simulation.simulationWidth, Simulation.simulationHeight)

  prefWidth = Simulation.simulationWidth
  prefHeight = Simulation.simulationHeight
  clip = clipRectangle
  styleClass = List("simulation-pane")
  hgrow = Always
  vgrow = Always

  this.width.onChange {
    Simulation.simulationWidth = this.width()
    clipRectangle.width = this.width()
  }
  this.height.onChange {
    Simulation.simulationHeight = this.height()
    clipRectangle.height = this.height()
  }


  //Sets a new goal for the leader by clicking the simulation
  this.setOnMouseClicked((event: MouseEvent) => {
    if (Simulation.leader.currentMovementType == Seek) {
      Simulation.updateGoal(event.getX, event.getY)
      Simulation.updateIndividualForces()
    }
    Platform.runLater(this.requestFocus())
    updatePositions()
  })


  //Initializing the leader
  private var leaderCircle = new Circle {
    radius = Simulation.leaderRadius
    fill = Color.Cyan
  }

  private var leaderVelocityArrow = new Line
  leaderVelocityArrow.stroke = Color.Black
  leaderVelocityArrow.strokeWidth = 2

  private var leaderCurrentForceArrow = new Line
  leaderCurrentForceArrow.stroke = Color.Purple
  leaderCurrentForceArrow.strokeWidth = 2

  private var leaderWallSeparationArrow = new Line
  leaderWallSeparationArrow.stroke = Color.Red


  //Initializing the leader's goal
  private var leaderGoal = Circle(5)
  leaderGoal.fill = Color.White


  private var followerCircles = new Group()
  private var followerVelocityArrows = new Group()
  private var followerFtlArrows = new Group()
  private var followerSeparationArrows = new Group()
  private var followerSectorArrows = new Group()

  resetPaneChildren()
  updateFollowerCount()


  //Sets the new individuals in the simulation
  def updateIndividualsFromFile() = {

    //Sets the new followers
    Simulation.numberOfFollowers = FileReader.individuals.length - 1
    Simulation.followers.clear()
    Simulation.updateLeader(FileReader.individuals.head.head, FileReader.individuals.head(1), FileReader.individuals.head(2), FileReader.individuals.head(3))

    //Creates the new followers from the file's data
    Simulation.followers.clear()
    for (data <- FileReader.individuals.tail) {
      Simulation.addFollower(data.head, data(1), data(2), data(3))
    }

    drawFollowerNodes()
    updatePositions()
  }


  //Spawns new followers to random locations if the currently drawn amount of followers doesn't match the number of followers.
  def updateFollowerCount() = {

    val currentlyDrawn = Simulation.followers.length

    if (currentlyDrawn != Simulation.numberOfFollowers) {
      if (currentlyDrawn < Simulation.numberOfFollowers) {
        for (i <- 1 to Simulation.numberOfFollowers - currentlyDrawn)
          Simulation.addFollower(
            Simulation.random.nextInt(Simulation.simulationWidth.toInt),
            Simulation.random.nextInt(Simulation.simulationHeight.toInt),
            Simulation.settings.fMaxSpeed - Simulation.random.nextDouble() * Simulation.settings.fMaxSpeed,
            Simulation.settings.fMaxSpeed - Simulation.random.nextDouble() * Simulation.settings.fMaxSpeed)
      } else {
        Simulation.followers.remove(Simulation.followers.length + Simulation.numberOfFollowers - currentlyDrawn, currentlyDrawn - Simulation.numberOfFollowers)
      }
      drawFollowerNodes()
    }
  }


  //Helper functions for other methods to reduce repeating code
  def resetPaneChildren() = {
    this.children.clear()
    this.children.add(followerVelocityArrows)
    this.children.add(followerCircles)
    this.children.add(followerFtlArrows)
    this.children.add(followerSeparationArrows)
    this.children.add(followerSectorArrows)
    this.children.add(new Group(leaderGoal, leaderCircle, leaderVelocityArrow, leaderCurrentForceArrow, leaderWallSeparationArrow))
  }


  //Draws all of the followers when the amount of followers is changed either manually or from a file.
  def drawFollowerNodes() = {

    followerCircles.children.clear()
    val temp1 = for (f <- Simulation.followers) yield Circle(f.location.x, f.location.y, Simulation.followerRadius)
    temp1.foreach(_.fill = Color.Pink)
    temp1.foreach(x => followerCircles.children.add(x))


    followerVelocityArrows.children.clear()
    if (Simulation.showFollowerVelocityArrows()) {
      val temp2 = for (f <- Simulation.followers) yield {
        Line(f.location.x,
          f.location.y,
          f.velocity.x * Simulation.velocityArrowLength + f.location.x,
          f.velocity.y * Simulation.velocityArrowLength + f.location.y)
      }
      temp2.foreach(_.stroke = Color.Black)
      temp2.foreach(x => followerVelocityArrows.children.add(x))
    }

    followerFtlArrows.children.clear()
    followerSeparationArrows.children.clear()
    followerSectorArrows.children.clear()
    if (Simulation.showFollowerForceArrows()) {
      val temp3 = for (f <- Simulation.followers) yield {
        Line(f.location.x,
          f.location.y,
          f.currentForces.head.x * Simulation.forceArrowLength + f.location.x,
          f.currentForces.head.y * Simulation.forceArrowLength + f.location.y)
      }
      temp3.foreach(_.stroke = Color.Blue)
      temp3.foreach(x => followerFtlArrows.children.add(x))


      val temp4 = for (f <- Simulation.followers) yield {
        Line(f.location.x,
          f.location.y,
          f.currentForces(1).x * Simulation.forceArrowLength + f.location.x,
          f.currentForces(1).y * Simulation.forceArrowLength + f.location.y)
      }
      temp4.foreach(_.stroke = Color.Green)
      temp4.foreach(x => followerSeparationArrows.children.add(x))


      if (Simulation.sectorForceEnabled()) {
        val temp5 = for (f <- Simulation.followers) yield {
          Line(f.location.x,
            f.location.y,
            f.currentForces(2).x * Simulation.forceArrowLength + f.location.x,
            f.currentForces(2).y * Simulation.forceArrowLength + f.location.y)
        }
        temp5.foreach(_.stroke = Color.Purple)
        temp5.foreach(x => followerSectorArrows.children.add(x))
      }
    }

  }

  //Moves the nodes of the individuals.
  def updatePositions() = {


    //moving all the circles
    leaderCircle.centerX = leader.location.x
    leaderCircle.centerY = leader.location.y

    leaderVelocityArrow.startX = leader.location.x
    leaderVelocityArrow.startY = leader.location.y
    leaderVelocityArrow.endX = (leader.velocity.x * Simulation.velocityArrowLength + leader.location.x)
    leaderVelocityArrow.endY = (leader.velocity.y * Simulation.velocityArrowLength + leader.location.y)

    leaderCurrentForceArrow.startX = leader.location.x
    leaderCurrentForceArrow.startY = leader.location.y
    leaderCurrentForceArrow.endX = (leader.currentForces.head.x * Simulation.forceArrowLength * 20 + leader.location.x)
    leaderCurrentForceArrow.endY = (leader.currentForces.head.y * Simulation.forceArrowLength * 20 + leader.location.y)

    leaderWallSeparationArrow.startX = leader.location.x
    leaderWallSeparationArrow.startY = leader.location.y
    leaderWallSeparationArrow.endX = (leader.currentForces(1).x * Simulation.forceArrowLength + leader.location.x)
    leaderWallSeparationArrow.endY = (leader.currentForces(1).y * Simulation.forceArrowLength + leader.location.y)

    if (Simulation.showGoal()) {
      leaderGoal.visible = true
      leaderGoal.centerX = leader.goal.x
      leaderGoal.centerY = leader.goal.y
    } else {
      leaderGoal.visible = false
    }

    if (Simulation.followers.nonEmpty) {
      val followers = Simulation.followers

      for (i <- 0 until Simulation.numberOfFollowers) {

        followerCircles.children(i) match {
          case c: jfxss.Circle =>
            c.centerX = followers(i).location.x
            c.centerY = followers(i).location.y
          case _ =>
        }

        if (Simulation.showFollowerVelocityArrows()) {
          followerVelocityArrows.children(i) match {
            case c: jfxss.Line =>
              c.startX = followers(i).location.x
              c.startY = followers(i).location.y
              c.endX = followers(i).velocity.x * Simulation.velocityArrowLength + followers(i).location.x
              c.endY = followers(i).velocity.y * Simulation.velocityArrowLength + followers(i).location.y
            case _ =>
          }
        }

        if (Simulation.showFollowerForceArrows()) {
          followerFtlArrows.children(i) match {
            case c: jfxss.Line =>
              c.startX = followers(i).location.x
              c.startY = followers(i).location.y
              c.endX = followers(i).currentForces.head.x * Simulation.forceArrowLength + followers(i).location.x
              c.endY = followers(i).currentForces.head.y * Simulation.forceArrowLength + followers(i).location.y
            case _ =>
          }

          followerSeparationArrows.children(i) match {
            case c: jfxss.Line =>
              c.startX = followers(i).location.x
              c.startY = followers(i).location.y
              c.endX = followers(i).currentForces(1).x * Simulation.forceArrowLength + followers(i).location.x
              c.endY = followers(i).currentForces(1).y * Simulation.forceArrowLength + followers(i).location.y
            case _ =>
          }

          if (Simulation.sectorForceEnabled()) {
            followerSectorArrows.children(i) match {
              case c: jfxss.Line =>
                c.startX = followers(i).location.x
                c.startY = followers(i).location.y
                c.endX = followers(i).currentForces(2).x * Simulation.forceArrowLength + followers(i).location.x
                c.endY = followers(i).currentForces(2).y * Simulation.forceArrowLength + followers(i).location.y
              case _ =>
            }
          }

        }

      }
    }

  }


}

