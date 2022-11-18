package ftlsimulator.gui.content

import ftlsimulator.Simulation
import ftlsimulator.file.{FileReader, FileWriter}
import javafx.scene.{shape => jfxss}
import scalafx.Includes._
import scalafx.beans.property.BooleanProperty
import scalafx.scene.Group
import scalafx.scene.input.{MouseButton, MouseEvent}
import scalafx.scene.layout.Pane
import scalafx.scene.layout.Priority.Always
import scalafx.scene.paint.Color
import scalafx.scene.shape.{Circle, Line, Rectangle}

import java.io.File
import scala.collection.mutable.Buffer

class EditorPane extends Pane {



  //Knows to prompt the user if the settings were changed
  var saved = true
  //Variables about the state of the editor
  var movingLeader = false
  var pressingMouse = BooleanProperty(false)


  private val individualCircles = new Group()
  private val individualVelocities = new Group()


  //initializes the editor with the leader
  addLeader(500, 500)

  //Parameters of the pane
  val clipRectangle = Rectangle(Simulation.simulationWidth, Simulation.simulationHeight)

  prefWidth = Simulation.simulationWidth
  prefHeight = Simulation.simulationHeight
  clip = clipRectangle
  styleClass = List("editor-pane")
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


  children = List(individualCircles, individualVelocities)


  //Creates a new circle or moves the leader when the mouse is pressed
  this.setOnMousePressed((event: MouseEvent) => {
    if (event.button == MouseButton.Primary) {
      pressingMouse() = true
      if (movingLeader) {
        moveLeader(event.getX, event.getY)
      } else {
        addFollowerCircle(event.getX, event.getY)
      }
    }
    if (event.button == MouseButton.Secondary) {
      removeIndividual(individualCircles.children.indexOf(event.target))
    }

  })

  //Draws the velocity arrow of the circle created when the mouse is released
  this.setOnMouseReleased((event: MouseEvent) => {
    if (event.button == MouseButton.Primary) {
      if (movingLeader)
        addVelocityArrows(individualCircles.children.head.asInstanceOf[jfxss.Circle], event.getX, event.getY)
      else
        addVelocityArrows(individualCircles.children.last.asInstanceOf[jfxss.Circle], event.getX, event.getY)

      pressingMouse() = false
    }
  })

  //Creates a new circle for the follower
  def addFollowerCircle(x: Double, y: Double) = {
    val circle = Circle(x, y, Simulation.followerRadius)
    circle.fill = Color.Pink
    individualCircles.children.add(circle)
    saved = false
  }

  //Changes the leaders location
  def moveLeader(x: Double, y: Double) = {
    individualCircles.children.head match {
      case c: jfxss.Circle =>
        c.centerX = x
        c.centerY = y
    }
    individualVelocities.children.head match {
      case c: jfxss.Line =>
        c.startX = x
        c.startY = y
        c.endX = x
        c.endY = y
    }
  }


  //Creates the velocity arrow for every individual
  def addVelocityArrows(c: jfxss.Circle, endX: Double, endY: Double): Unit = {

    val hypotenuse = math.sqrt((endX - c.centerX()) * (endX - c.centerX()) + (endY - c.centerY()) * (endY - c.centerY()))
    val arrowLength = 20
    val velocityArrow = Line(
      c.centerX(),
      c.centerY(),
      if (hypotenuse < arrowLength)
        endX
      else
        c.centerX() + arrowLength * math.cos(math.atan((endY - c.centerY()) / (endX - c.centerX()))) * (if (endX < c.centerX()) -1 else 1),
      if (hypotenuse < arrowLength)
        endY
      else
        c.centerY() + arrowLength * math.sin(math.atan((endY - c.centerY()) / (endX - c.centerX()))) * (if (endX < c.centerX()) -1 else 1))
    velocityArrow.stroke = Color.Black
    if (c == individualCircles.children.head.asInstanceOf[jfxss.Circle])
      individualVelocities.children(0) = velocityArrow
    else
      individualVelocities.children.add(velocityArrow)
    saved = false
  }

  def removeIndividual(index: Int): Unit = {
    if (index != -1) {
      saved = false
      if (index == 0) {
        individualCircles.children(0) match {
          case c: jfxss.Circle =>
            individualVelocities.children(0) = Line(
              c.centerX(),
              c.centerY(),
              c.centerX(),
              c.centerY())
        }
      } else {
        individualCircles.children.remove(individualCircles.children(index))
        individualVelocities.children.remove(individualVelocities.children(index))
      }
    }
  }

  //Creates the leader when the editor is initialized or a file is run
  def addLeader(x: Double, y: Double) = {
    val leaderCircle = Circle(x, y, Simulation.leaderRadius)
    leaderCircle.fill = Color.Cyan
    individualCircles.children.add(leaderCircle)

    val leaderVelocity = Line(
      leaderCircle.centerX(),
      leaderCircle.centerY(),
      leaderCircle.centerX(),
      leaderCircle.centerY()
    )
    leaderVelocity.stroke = Color.Black
    individualVelocities.children.add(leaderVelocity)
  }

  //Clears the current editor and restarts it from a file
  def updateIndividualsFromFile() = {
    individualCircles.children.clear()
    individualVelocities.children.clear()
    addLeader(FileReader.individuals.head.head, FileReader.individuals.head(1))
    addVelocityArrows(individualCircles.children.last.asInstanceOf[jfxss.Circle], FileReader.individuals.head.head + FileReader.individuals.head(2), FileReader.individuals.head(1) + FileReader.individuals.head(3))
    for (f <- FileReader.individuals.tail) {
      addFollowerCircle(f.head, f(1))
      addVelocityArrows(individualCircles.children.last.asInstanceOf[jfxss.Circle], f.head + f(2), f(1) + f(3))
    }
    saved = true
  }

  //Saves the information from the simulation to a file
  def saveToFile(file: File) = {
    val individualData = for (i <- 0 until individualVelocities.children.length) yield {
      individualVelocities.children(i) match {
        case l: jfxss.Line => Buffer(l.startX(), l.startY(), l.endX() - l.startX(), l.endY() - l.startY())
      }
    }

    FileWriter.writeToFile(file, individualData.toBuffer, Simulation.editorSettings)
    if (FileWriter.succeeded) saved = true
  }

}
