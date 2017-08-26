// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

/**
  * Possible status of the cell. determined by what's in it.
  * @author Barry Becker
  */
object CellStatus extends Enumeration {
  type CellStatus = Value
  val EMPTY = Value(".")
  val SURFACE = Value("S")
  val FULL = Value("F")
  val OBSTACLE = Value("o")
  val ISOLATED = Value("I")
}
