package ftlsimulator.util

import scala.math.{pow, sqrt}

class VectorVariable(xVal: Double, yVal: Double) {

  val x: Double = xVal
  val y: Double = yVal


  //Distance between two points in space
  def distance(v: VectorVariable): Double =
    sqrt(pow(this.x - v.x, 2) + pow(this.y - v.y, 2))

  def distanceSquared(v: VectorVariable) =
    pow(this.x - v.x, 2) + pow(this.y - v.y, 2)

  //Length of a vector
  def magnitude() =
    sqrt(this.x * this.x + this.y * this.y)

  //To save resources in certain situations
  def magnitudeSquared() =
    this.x * this.x + this.y * this.y

  //Returns a vector of a given magnitude
  def withMagnitude(mag: Double): VectorVariable = {
    val z = magnitudeSquared()
    if (z != 0) {
      this mul (1 / sqrt(z)) mul mag
    } else
      this
  }

  //Returns a vectorvariable that is limited to the magnitude max
  def limitMagnitude(max: Double): VectorVariable = {
    if (this.magnitudeSquared() > max * max) {
      this withMagnitude (max)
    } else {
      this
    }
  }

  def limitBottomMagnitude(min: Double): VectorVariable = {
    if (this.magnitudeSquared() < min * min) {
      this withMagnitude (min)
    } else {
      this
    }
  }

  //Returns a VectorVariable which's values are limited to a certain range
  def limitXToRange(bot: Double, top: Double): VectorVariable =
    new VectorVariable(x.max(bot).min(top), y)

  def limitYToRange(bot: Double, top: Double): VectorVariable =
    new VectorVariable(x, y.max(bot).min(top))

  //dot product
  def dotProduct(v: VectorVariable): Double =
    this.x * v.x + this.y * v.y

  def projectTo(v: VectorVariable): VectorVariable =
    v mul (v.dotProduct(this) / v.magnitudeSquared())

  def addxandy(x: Double, y: Double): VectorVariable =
    new VectorVariable(this.x + x, this.y + y)

  def addx(x: Double): VectorVariable =
    new VectorVariable(this.x + x, this.y)

  def addy(y: Double): VectorVariable =
    new VectorVariable(this.x, this.y + y)

  def subx(x: Double): VectorVariable =
    new VectorVariable(this.x - x, this.y)

  def suby(y: Double): VectorVariable =
    new VectorVariable(this.x, this.y - y)

  def sub(v: VectorVariable): VectorVariable =
    new VectorVariable(this.x - v.x, this.y - v.y)

  def add(v: VectorVariable): VectorVariable =
    new VectorVariable(this.x + v.x, this.y + v.y)

  def mul(v: VectorVariable): VectorVariable =
    new VectorVariable(this.x * v.x, this.y * v.y)

  def mul(d: Double): VectorVariable =
    new VectorVariable(this.x * d, this.y * d)

  def mulx(d: Double): VectorVariable =
    new VectorVariable(this.x * d, this.y)

  def muly(d: Double): VectorVariable =
    new VectorVariable(this.x, this.y * d)

  //Swap the x and y values
  def swap(): VectorVariable =
    new VectorVariable(this.y, this.x)

  //returns the vector turned 90 degrees to the right
  def turnRight(): VectorVariable =
    this.swap().mulx(-1)

  //returns the vector turned 90 degrees to the left
  def turnLeft(): VectorVariable =
    this.swap().muly(-1)

  //returns the vector turned to the opposite direction
  def turnOpposite(): VectorVariable =
    this.mul(-1)


  override def toString: String = s"VectorVariable(${x}, ${y})"

}

object VectorVariable {

  //formula for finding the area of a triangle with three points.
  def triangleArea(p1: VectorVariable, p2: VectorVariable, p3: VectorVariable): Double =
    (p1.x * (p2.y - p3.y) + p2.x * (p3.y - p1.y) + p3.x * (p1.y - p2.y)) / 2

  //Returns the vector from the start point to the end point
  def pointToPoint(start: VectorVariable, end: VectorVariable) =
    new VectorVariable(end.x - start.x, end.y - start.y)

  def pointRelativeTo(base: VectorVariable, point: VectorVariable) =
    new VectorVariable(point.x - base.x, base.y - point.y)


  //Used for checking which side of the line v2 -> v3 the point p is.
  def findWhichSide(p: VectorVariable, v2: VectorVariable, v3: VectorVariable) =
    (p.x - v3.x) * (v2.y - v3.y) - (v2.x - v3.x) * (p.y - v3.y)

  //Checks if a point is on the same side of three sides of a triangle. If so, it is inside the triangle.
  def insideTriangle(p: VectorVariable, v1: VectorVariable, v2: VectorVariable, v3: VectorVariable) = {
    //Checks for every side of the triangle the value of sign.
    val s1 = findWhichSide(p, v1, v2)
    val s2 = findWhichSide(p, v2, v3)
    val s3 = findWhichSide(p, v3, v1)

    //If the point is inside the triangle, all of the values of the sides have to have the same sign.
    //If at least one of the values is a different sign, we know that the point can't be inside the triangle.
    val containsNegative = (s1 < 0) || (s2 < 0) || (s3 < 0)
    val containsPositive = (s1 > 0) || (s2 > 0) || (s3 > 0)

    //Check if the values achieve this requirement.
    !(containsNegative && containsPositive)
  }


  def apply() = new VectorVariable(0, 0)

}