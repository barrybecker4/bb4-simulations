// Copyright by Barry G. Becker, 2016-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.plugin

import com.barrybecker4.common.util.PackageReflector
import java.io.IOException
import java.lang.reflect.Modifier
import java.security.AccessControlException
import java.util

import com.barrybecker4.simulation.trading.model.generationstrategy.GenerationStrategy

/**
  * Finds plugins for type of stock series generation strategies.
  * Can be used to populate a dropdown.
  * Uses reflection to dynamically load available strategies.
  * @param packageName name of the package to get the plugin classes from
  *              Something like "com.barrybecker4.simulation.trading.model.generationstrategy"
  * @author Barry Becker
  */
class StrategyPlugins[E <: StrategyPlugin](val packageName: String, val clzz: Class[_],
                                           val defaultStrategies: util.List[E]) {
  private val strategyNames = new util.ArrayList[String]
  private val valueMap = new util.HashMap[String, E]

  try {
    val strategyClasses: util.List[Class[_]] = new PackageReflector().getClasses(packageName)
    for (ci <- 0 until strategyClasses.size()) {
      val c = strategyClasses.get(ci)
      // Skip the abstract class (if any) because it cannot (and should not) be instantiated.
      if (!Modifier.isAbstract(c.getModifiers) && clzz.isAssignableFrom(c)) {
        val strategy = c.newInstance.asInstanceOf[E]
        val name = strategy.name
        strategyNames.add(name)
        valueMap.put(name, strategy)
      }
    }
    println("Trading strategy plugins found: " + valueMap)
  } catch {
    case e@(_: AccessControlException | _: NullPointerException) =>
      // fallback to local strategies if cannot access filesystem (as for applet or webstart)
      println("Unable to access filesystem to load plugin. Will use default strategies only")
      for (si <- 0 until defaultStrategies.size()) {
        val s = defaultStrategies.get(si)
        strategyNames.add(s.name)
        valueMap.put(s.name, s)
      }
      println("default strategies are : " + valueMap)
    case e@(_: ClassNotFoundException | _: InstantiationException | _: IOException | _: IllegalAccessException) =>
      e.printStackTrace()
  }

  def getStrategies: util.List[String] = strategyNames

  /** Create an instance of the algorithm given the controller and a refreshable. */
  def getStrategy(name: String): E = {
    if (!valueMap.containsKey(name))
      println("Could not find strategy with name " + name + " among " + valueMap)
    valueMap.get(name)
  }
}
