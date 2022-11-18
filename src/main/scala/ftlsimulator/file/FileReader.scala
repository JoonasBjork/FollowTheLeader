package ftlsimulator.file

import ftlsimulator.Simulation
import scalafx.beans.property.BooleanProperty

import java.io.{BufferedReader, FileNotFoundException, FileReader, IOException}
import scala.collection.mutable.Buffer

object FileReader {

  var individuals = Buffer[Buffer[Double]]()
  var parameters = Buffer[Double]()

  var readSucceeded = BooleanProperty(false)
  var errorMessage = ""

  // Reads the file if it contains the correct data. Otherwise throws an error.
  def readFile(filePath: String) = {
    readSucceeded() = false
    try {
      if (filePath.split("\\.").toBuffer.last != "csv")
        throw new InvalidFileExtensionException(
          filePath.split("\\.").toBuffer.last
        )

      individuals.clear()
      parameters.clear()

      val fileIn = new FileReader(filePath)
      val linesIn = new BufferedReader(fileIn)
      try {
        // Skip first line
        linesIn.readLine()
        // Collect the parameters of the simulation
        parameters =
          linesIn.readLine().split(",").map(_.trim).map(_.toDouble).toBuffer
        if (parameters.length != 8)
          throw IncorrectNumberOfParametersException(parameters.length)
        if (!parametersAreCorrect()) {
          throw ParametersOutOfRangeException()
        }
        // Skip the third line
        linesIn.readLine()
        // Read the individual data
        var oneLine = linesIn.readLine()
        while (oneLine != null) {

          if (oneLine.replaceAll(" ", "").nonEmpty) {
            val individualData =
              oneLine.split(",").map(_.trim).map(_.toDouble).toBuffer.take(4)
            individuals += (individualData ++ Buffer.fill(
              4 - individualData.length
            )(0.0))
          }

          if (individuals.length > Simulation.maxFollowers + 1)
            throw TooManyFollowersException(oneLine)

          oneLine = linesIn.readLine()
        }

        if (individuals.isEmpty)
          throw NoLeaderException()

        readSucceeded() = true

      } finally {
        fileIn.close()
        linesIn.close()
      }
    } catch {
      case e: IOException =>
        errorMessage = e.toString
      case e: FileNotFoundException =>
        errorMessage = e.toString
      case e: NumberFormatException =>
        errorMessage = e.toString
      case e: InvalidFileExtensionException =>
        errorMessage =
          s"File extension should be ${'"'}.csv${'"'} but was ${'"' + "." + e.fileExtension + '"'}"
      case e: TooManyFollowersException =>
        errorMessage =
          s"Too many followers. First non-accepted follower was: ${e.followerInformation}"
      case e: NoLeaderException =>
        errorMessage = "File doesn't contain a leader"
      case e: IncorrectNumberOfParametersException =>
        errorMessage =
          s"File contains the wrong amount of parameters (${e.number}) while should contain (8)"
      case e: ParametersOutOfRangeException =>
        errorMessage = "One or more of the parameters are out of bounds"
    }
  }

  // Check that the parameters of the file are in the range allowed in Simulation.
  def parametersAreCorrect(): Boolean = {
    (isBetween(parameters.head, Simulation.minSpeed, Simulation.maxSpeed)
    && isBetween(parameters(1), Simulation.minForce, Simulation.maxForce)
    && isBetween(
      parameters(2),
      Simulation.minWallAvoidRange,
      Simulation.maxWallAvoidRange
    )
    && isBetween(
      parameters(3),
      Simulation.minArriveRange,
      Simulation.maxArriveRange
    )
    && isBetween(parameters(4), Simulation.minSpeed, Simulation.maxSpeed)
    && isBetween(parameters(5), Simulation.minForce, Simulation.maxForce)
    && isBetween(
      parameters(6),
      Simulation.minNearbyRange,
      Simulation.maxNearbyRange
    )
    && isBetween(
      parameters(7),
      Simulation.minArriveRange,
      Simulation.maxArriveRange
    ))
  }

  // Helpoer method to find if a value is between two values
  def isBetween(value: Double, min: Double, max: Double): Boolean =
    value >= min && value <= max

}

case class InvalidFileExtensionException(fileExtension: String)
    extends java.lang.Exception

case class TooManyFollowersException(followerInformation: String)
    extends java.lang.Exception

case class NoLeaderException() extends java.lang.Exception

case class IncorrectNumberOfParametersException(number: Int)
    extends java.lang.Exception

case class ParametersOutOfRangeException() extends java.lang.Exception
