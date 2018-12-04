// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trading.model.runner

import com.barrybecker4.common.math.function.HeightFunction
import com.barrybecker4.simulation.trading.options.StockGenerationOptions
import com.barrybecker4.simulation.trading.options.TradingOptions


/**
  * Given a time series representing a stock's (or collection of stocks) price, calculate the expected gain (or loss)
  * by applying some trading strategy.
  * @author Barry Becker
  */
class StockRunner(var tradingOpts: TradingOptions) {

  /** @return everything about the run including the time series for the stock, the amounts invested in the stock and
    *         in the reserve account, and the amount of gain (or loss if negative) achieved by applying a certain
    *         trading strategy to a generated time series simulating a changing stock price over time.
    */
  def doRun(generationOpts: StockGenerationOptions): StockRunResult = {
    val generationStrategy = generationOpts.generationStrategy
    val tradingStrategy = tradingOpts.tradingStrategy
    val numPeriods = generationOpts.numTimePeriods

    // initial buy
    val stockPrices = generationStrategy.getSeries(generationOpts.startingValue, numPeriods)
    var position = tradingStrategy.initialInvestment(stockPrices(0),
      tradingOpts.startingTotal, tradingOpts.startingInvestmentPercent)
    val investValues = new Array[Double](numPeriods + 1)
    val reserveValues = new Array[Double](numPeriods + 1)


    for (i <- 0 until numPeriods) {
      investValues(i) = position.invested
      reserveValues(i) = position.reserve
      position = tradingStrategy.updateInvestment(stockPrices(i))
    }
    position = tradingStrategy.finalizeInvestment(stockPrices(numPeriods))
    investValues(numPeriods) = 0
    reserveValues(numPeriods) = position.reserve
    //println("*** final sell = " + finalSell
    //        + " reserve = " + reserve + " totalGain = " + totalGain + " ending stock price = " + stockPrice);

    new StockRunResult(
      new HeightFunction(stockPrices.toArray),
      new HeightFunction(investValues),
      new HeightFunction(reserveValues), tradingStrategy.getGain)
  }
}
