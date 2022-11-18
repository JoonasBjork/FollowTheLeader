package ftlsimulator.entity

import ftlsimulator.util.VectorVariable

trait Individual {


  //basic variables of an individual
  var velocity: VectorVariable
  var location: VectorVariable
  val indexInLine: Int


  def move(): Unit



}
