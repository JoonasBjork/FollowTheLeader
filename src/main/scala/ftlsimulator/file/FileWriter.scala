package ftlsimulator.file

import ftlsimulator.Settings

import java.io.{BufferedWriter, File, FileNotFoundException, FileWriter}
import scala.collection.mutable

object FileWriter {

  var succeeded = false


  def writeToFile(file: File, individualData: mutable.Buffer[mutable.Buffer[Double]], s: Settings) {
    succeeded = false
    try {
      val fw = new FileWriter(file)
      val bw = new BufferedWriter(fw)
      try {
        bw.write("LeaderMaxSpeed, LeaderSteeringForce, LeaderWallAvoidRange, LeaderArriveRange, FollowerMaxSpeed, FollowerSteeringForce, FollowerNearbyRange, FollowerArrivalRange")
        bw.newLine()

        //Writes the parameters of the simulation
        bw.write(s"${s.lMaxSpeed}, " +
          s"${s.lSteeringForce}, " +
          s"${s.lWallAvoidRange}, " +
          s"${s.lArriveRange}, " +
          s"${s.fMaxSpeed}, " +
          s"${s.fSteeringForce}, " +
          s"${s.fNearbyRange}, " +
          s"${s.fArriveRange}")
        bw.newLine()
        bw.write("xPos, yPos, xVel, yVel -- First one is the leader")
        //Writes the individuals
        for (line <- individualData) {
          bw.newLine()
          bw.write(s"${line.head}, ${line(1)}, ${line(2)}, ${line(3)}")
        }

        succeeded = true
      } finally {
        bw.close()
      }
    } catch {
      case e: FileNotFoundException =>
    }
  }


}
