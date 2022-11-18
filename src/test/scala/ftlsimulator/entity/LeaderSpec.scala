package ftlsimulator.entity

import ftlsimulator.Simulation
import org.scalatest.flatspec.AnyFlatSpec

class LeaderSpec extends AnyFlatSpec {
  "Leader's object avoid force" should "be greater if the leader is closer to the wall" in {
    Simulation.updateLeader(500, 10, 0, 0)
    var f1 = Simulation.leader.obAvoidForce().magnitude()
    Simulation.updateLeader(970, 500, 0, 0)
    var f2 = Simulation.leader.obAvoidForce().magnitude()
    assert(f1 > f2)

    Simulation.updateLeader(20, 500, 0, 0)
    f1 = Simulation.leader.obAvoidForce().magnitude()
    Simulation.updateLeader(500, 500, 0, 0)
    f2 = Simulation.leader.obAvoidForce().magnitude()
    assert(f1 > f2)


    //Force should be 0 when leader is not near a wall
    Simulation.updateLeader(500, 500, 0, 0)
    f1 = Simulation.leader.obAvoidForce().magnitude()
    assert(f1 == 0)

  }




}
