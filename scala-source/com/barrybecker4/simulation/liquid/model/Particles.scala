// Copyright by Barry G. Becker, 2016-2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.liquid.model

import scala.collection.immutable.HashSet
import scala.util.Random

/**
  * A set of particles in a grid. These particles represent the fluid.
  * @author Barry Becker
  */
object Particles {
  /** Ensure that the runs are the same  */
  private val RANDOM = new Random(1)
}

class Particles {

  var set: HashSet[Particle] = HashSet()

  /**
    * @param x            cell location x
    * @param y            cell location y
    * @param numParticles number of particles to add.
    * @param grid         some grid to add to.
    */
  private[model] def addRandomParticles(x: Double, y: Double, numParticles: Int, grid: Grid): Unit = {
    for (i <- 0 until numParticles)
      addParticle(x + Particles.RANDOM.nextDouble(), y + Particles.RANDOM.nextDouble(), grid)
  }

  def addParticle(x: Double, y: Double, grid: Grid): Unit = {
    val cell = grid.getCell(x.toInt, y.toInt)
    val p = new Particle(x, y, cell)
    set += p
    cell.incParticles()
  }

  def iterator: Iterator[Particle] = set.iterator
  def contains(p: Particle): Boolean = set.contains(p)
}
