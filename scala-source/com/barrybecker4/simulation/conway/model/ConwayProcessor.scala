// Copyright by Barry G. Becker, 2014-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.conway.model

import com.barrybecker4.common.geometry.IntLocation
import com.barrybecker4.common.geometry.Location
import com.barrybecker4.simulation.conway.model.rules._
import ConwayProcessor.RuleType.RuleType
import ConwayProcessor.RuleType.{SwarmB356S23, TraditionalB3S23, AmoebaB34S456, HighlifeB36S23}


/**
  * @author Barry Becker
  */
object ConwayProcessor {

  val DEFAULT_USE_PARALLEL = true

  object RuleType extends Enumeration {
    type RuleType = Value
    val TraditionalB3S23, AmoebaB34S456, HighlifeB36S23, SwarmB356S23 = Value
  }

  val DEFAULT_RULE_TYPE = RuleType.TraditionalB3S23

  def main(args: Array[String]): Unit = {
    val cave = new ConwayProcessor(DEFAULT_USE_PARALLEL)
    cave.nextPhase()
  }
}

class ConwayProcessor private[model](val useParallel: Boolean) {
  private var conway = new Conway
  conway.initialize()
  setUseParallel(useParallel)
  private var wrapGrid = false
  private var width = -1
  private var height = -1
  private var rule: Rule = new RuleB3S23 // new RuleB34S456Amoeba();

  /** Constructor that allows you to specify the dimensions of the conway */
  def this() {
    this(ConwayProcessor.DEFAULT_USE_PARALLEL)
  }

  private[model] def setWrap(wrapGrid: Boolean, width: Int, height: Int) = {
    this.wrapGrid = wrapGrid
    this.width = width
    this.height = height
  }

  private[model] def setRuleType(ruleType: RuleType) = {
    rule = ruleType match {
      case TraditionalB3S23 => new RuleB3S23
      case AmoebaB34S456 => new RuleB34S456Amoeba
      case HighlifeB36S23 => new RuleB36S23Highlife
      case SwarmB356S23 => new RuleB356S23Swarm
    }
  }

  private[model] def setUseParallel(parallelized: Boolean) = {
    //parallelizer =
    //     parallelized ? new RunnableParallelizer() : new RunnableParallelizer(1);
  }

  def getPoints: Set[Location] = conway.getPoints

  private[model] def setAlive(row: Int, col: Int) = {
    conway.setValue(new IntLocation(row, col), 1)
  }

  /**
    * Compute the next step of the simulation.
    */
  private[model] def nextPhase() = {
    val newConway = new Conway
    newConway.setWrapping(wrapGrid, width, height)
    conway = rule.applyRule(conway, newConway)
    /*
            int numThreads = parallelizer.getNumThreads();
            List<Runnable> workers = new ArrayList<>(numThreads + 1);
            int range = conway.getNumPoints() / numThreads;
            for (int i = 0; i < numThreads; i++) {
                int offset = i * range;
                workers.add(new Worker(offset, offset + range, newConway));
            }

            // blocks until all Callables are done running.
            parallelizer.invokeAllRunnables(workers);
            */
  }

  def getValue(c: Location): Integer = conway.getValue(c)

  override def toString: String = conway.toString
}