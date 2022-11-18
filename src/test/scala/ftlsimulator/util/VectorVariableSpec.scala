package ftlsimulator.util

import org.scalatest.flatspec.AnyFlatSpec
import org.scalactic.Tolerance._

class VectorVariableSpec extends AnyFlatSpec {

  "Magnitude" should "be greated for longer vectors" in {
    assert(new VectorVariable(1, 0).magnitude < new VectorVariable(2, 0).magnitude)
    assert(new VectorVariable(1, 1).magnitude < new VectorVariable(2, 2).magnitude)
    assert(new VectorVariable(2, 0).magnitude < new VectorVariable(-3, 1).magnitude)
    assert(new VectorVariable(7, 7).magnitude < new VectorVariable(-20, -20).magnitude)
    assert(new VectorVariable(0, 0).magnitude() == 0)
  }

  "withMagnitude" should "make the vector's magnitude the specified amount" in {
    assert(new VectorVariable(1, 0).withMagnitude(20).magnitude() == 20)
    assert(new VectorVariable(50, 1).withMagnitude(14).magnitude() === 14.0 +- 0.01)
    assert(new VectorVariable(-1346, 6453).withMagnitude(69).magnitude() === 69.0 +- 0.01)
    assert(new VectorVariable(20, 20).withMagnitude(0).magnitude() == 0)
  }



}
