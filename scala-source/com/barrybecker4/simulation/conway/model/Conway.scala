// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.common.geometry.Location
import java.util.concurrent.ConcurrentHashMap

import scala.collection.immutable.Set
import scala.util.Random


/**
  * The data for points in the conway life simulation
  *
  * @author Barry Becker
  */
object Conway {
  private val NBR_OFFSETS = Array[Location](
    new IntLocation(-1, -1),
    new IntLocation(-1, 0),
    new IntLocation(-1, 1),
    new IntLocation(0, -1),
    new IntLocation(0, 1),
    new IntLocation(1, -1),
    new IntLocation(1, 0),
    new IntLocation(1, 1)
  )
}

class Conway private[model]() {
  private var points = Map[Location, Integer]()

  /** Since its on an infinite grid. Only store the grid locations where there his life. */
  private var wrap = false
  private var width = -1
  private var height = -1

  private[model] def setWrapping(wrap: Boolean, width: Int, height: Int) = {
    this.wrap = wrap
    this.width = width
    this.height = height
  }

  def initialize(): Unit = { //genMap(100, 100);
    addGlider()
  }

  def getCandidates: Set[Location] = {
    var candidates = Set[Location]()
    for (c <- points.keys) {
      candidates += keepInBounds(c)
      for (offset <- Conway.NBR_OFFSETS) {
        candidates += keepInBounds(c.incrementOnCopy(offset))
      }
    }
    candidates
  }

  private def keepInBounds(c: Location): Location =
    if (wrap) new IntLocation((c.getRow + height) % height, (c.getCol + width) % width) else c

  private[model] def getPoints = points.keySet

  def isAlive(coord: Location): Boolean = points.contains(coord)

  def getNumNeighbors(c: Location): Int = {
    var numNbrs = 0
    for (offset <- Conway.NBR_OFFSETS) {
      if (isAlive(keepInBounds(c.incrementOnCopy(offset)))) numNbrs += 1
    }
    numNbrs
  }

  def setValue(coord: Location, value: Int): Unit = {
    points += coord ->value
  }

  def getValue(coord: Location): Integer = points(coord)

  private def addGlider() = {
    setValue(new IntLocation(10, 10), 1)
    setValue(new IntLocation(11, 11), 1)
    setValue(new IntLocation(11, 12), 1)
    setValue(new IntLocation(10, 12), 1)
    setValue(new IntLocation(9, 12), 1)
  }

  /** generate the initial random 2D data */
  private def genMap(width: Int, length: Int) = {
    val RAND = new Random(1)
    points = Map[Location, Integer]()
    for (x <- 0 until width) {
      for (y <- 0 until length) {
        val r = RAND.nextDouble
        if (r > 0.7) setValue(new IntLocation(y, x), 1)
      }
    }
  }
}
