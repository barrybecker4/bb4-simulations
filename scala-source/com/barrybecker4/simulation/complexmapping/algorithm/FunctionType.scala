/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT*/
package com.barrybecker4.simulation.complexmapping.algorithm

import com.barrybecker4.simulation.complexmapping.algorithm.functions._


/**
  * Some functions that we can use to map complex numbers to new locations.
  * @author Barry Becker
  */
object FunctionType extends Enumeration {

  case class Val(name: String, function: ComplexFunction) extends super.Val
  implicit def valueToFunctionTypeVal(x: Value): Val = x.asInstanceOf[Val]

  val IDENTITY = Val("Identity", IdentityFunction())
  val INT_POWER = Val("s ^ n, n is int; s is complex", IntPowerFunction())
  val POWER = Val("n ^ s, n is int; s is complex", PowerFunction())
  val GAMMA = Val("Gamma", GammaFunction())
  val RIEMANN_ZETA = Val("Riemann Zeta", RiemannZetaFunction())
  val DIRICHLET_ETA = Val("Dirichlet Eta", DirichletEtaFunction())
  val ZETA_ANALYTIC_EXTENSION = Val("Analytic extension of Zeta", ZetaAnalyticExtensionFunction())

  val VALUES: Array[Val] = Array(
    IDENTITY, INT_POWER, POWER, GAMMA, RIEMANN_ZETA, DIRICHLET_ETA, ZETA_ANALYTIC_EXTENSION
  )
}
