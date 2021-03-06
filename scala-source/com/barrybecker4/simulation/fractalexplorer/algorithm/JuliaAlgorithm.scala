/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.fractalexplorer.algorithm

import com.barrybecker4.math.complex.ComplexNumber
import com.barrybecker4.math.complex.ComplexNumberRange
import JuliaAlgorithm._

/**
  * Populates the FractalModel using the iterative Julia set algorithm.
  *
  * @author Barry Becker
  */
object JuliaAlgorithm {
  val DEFAULT_JULIA_SEED: ComplexNumber = new ComplexNumber(0.233, 0.5378)
  private val INITIAL_RANGE: ComplexNumberRange =
    ComplexNumberRange(new ComplexNumber(-1.8, -1.7), new ComplexNumber(1.8, 1.7))
}

class JuliaAlgorithm extends FractalAlgorithm(JuliaAlgorithm.INITIAL_RANGE) {

  private var seed: ComplexNumber = JuliaAlgorithm.DEFAULT_JULIA_SEED

  def setJuliaSeed(seed: ComplexNumber): Unit = {
    println("setting jSeed to " + seed)
    this.seed = seed
  }

  def getFractalValue(initialValue: ComplexNumber): Double = {
    var z: ComplexNumber = initialValue
    var numIterations: Int = 0
    while (z.getMagnitude < 2.0 && numIterations < getMaxIterations) {
      z = z.power(2).add(seed)
      numIterations += 1
    }
    numIterations.toDouble / getMaxIterations
  }
}
