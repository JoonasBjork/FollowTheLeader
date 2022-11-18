package ftlsimulator.entity

import ftlsimulator.Simulation
import org.scalatest.flatspec.AnyFlatSpec

class FollowerSpec extends AnyFlatSpec {
  "Follower's separation force" should "be larger when closer to leader" in {
    Simulation.updateLeader(150, 150, 0, 0)

    Simulation.addFollower(100, 100, 0, 0)
    Simulation.addFollower(201, 201, 0, 0)
    assert(Simulation.followers.head.separationForce().magnitude() > Simulation.followers(1).separationForce().magnitude())
    Simulation.followers.clear()

    Simulation.addFollower(100, 100, 0, 0)
    Simulation.addFollower(800, 800, 0, 0)
    assert(Simulation.followers.head.separationForce().magnitude() > Simulation.followers(1).separationForce().magnitude())
    Simulation.followers.clear()

    Simulation.addFollower(140, 100, 0, 0)
    Simulation.addFollower(170, 100, 0, 0)
    assert(Simulation.followers.head.separationForce().magnitude() > Simulation.followers(1).separationForce().magnitude())
    Simulation.followers.clear()
  }

  "Follower's sector avoid force" should "not be zero when in front of the leader" in {

    Simulation.sectorForceEnabled() = true
    //Leader facing right
    Simulation.updateLeader(150, 150, 5, 0)
    //On the right of the leader
    Simulation.addFollower(155, 150, 0, 0)
    //Below the leader
    Simulation.addFollower(150, 155, 0, 0)
    //On the left of the leader
    Simulation.addFollower(145, 150, 0, 0)
    //On top of the leader
    Simulation.addFollower(150, 145, 0, 0)

    Simulation.updateIndividualForces()
    assert(Simulation.followers.head.sectorAvoidForce().magnitude != 0.0)
    assert(Simulation.followers(1).sectorAvoidForce().magnitude == 0.0)
    assert(Simulation.followers(2).sectorAvoidForce().magnitude == 0.0)
    assert(Simulation.followers(3).sectorAvoidForce().magnitude == 0.0)
  }

  "Follower's separation force" should "be bigger for individuals that are closer to each other" in {
    Simulation.followers.clear()
    Simulation.updateLeader(500, 500, 0, 0)
    Simulation.addFollower(495, 500, 0, 0)
    Simulation.addFollower(600, 500, 0, 0)

    Simulation.updateIndividualForces()
    assert(Simulation.followers.head.separationForce().magnitude > Simulation.followers(1).separationForce().magnitude)

    Simulation.followers.clear()
    Simulation.addFollower(700, 700, 0, 0)
    Simulation.addFollower(200, 200, 0, 0)
    assert(Simulation.followers.head.separationForce().magnitude > Simulation.followers(1).separationForce().magnitude)


  }

  "Follower's separation force" should "be equal for the same distance" in {
    Simulation.followers.clear()
    Simulation.addFollower(200, 500, 0, 0)
    Simulation.addFollower(500, 800, 0, 0)
    assert(Simulation.followers.head.separationForce().magnitude == Simulation.followers(1).separationForce().magnitude)
  }

}
