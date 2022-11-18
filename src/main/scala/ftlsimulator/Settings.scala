package ftlsimulator

import ftlsimulator.file.FileReader.parameters

class Settings {

  //Adjustable parameters for the leader
  var lMaxSpeed = 5.0
  var lSteeringForce = 0.5
  var lWallAvoidRange = 100
  var lArriveRange = 50

  //Adjustable parameters for the followers
  var fMaxSpeed = 5.0
  var fSteeringForce = 0.7
  var fNearbyRange = 200
  var fArriveRange = 50

  //Sets the new settings from a file
  def updateSettingsFromFile() = {
    this.lMaxSpeed = parameters.head
    this.lSteeringForce = parameters(1)
    this.lWallAvoidRange = parameters(2).toInt
    this.lArriveRange = parameters(3).toInt
    this.fMaxSpeed = parameters(4)
    this.fSteeringForce = parameters(5)
    this.fNearbyRange = parameters(6).toInt
    this.fArriveRange = parameters(7).toInt
  }
}
