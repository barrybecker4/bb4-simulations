// Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model.kernal

/**
  * Uses a kernal of some sort to count neighbors in a 2D grid.
  *
  * @author Barry Becker
  */
trait Kernel {
  def countNeighbors(x: Int, y: Int): Double
}
