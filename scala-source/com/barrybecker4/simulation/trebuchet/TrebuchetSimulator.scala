// Copyright by Barry G. Becker, 2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.trebuchet

import com.barrybecker4.common.concurrency.ThreadUtil
import com.barrybecker4.common.util.FileUtil
import com.barrybecker4.optimization.Optimizer
import com.barrybecker4.optimization.parameter.NumericParameterArray
import com.barrybecker4.optimization.parameter.ParameterArray
import com.barrybecker4.optimization.parameter.types.Parameter
import com.barrybecker4.optimization.strategy.OptimizationStrategyType
import com.barrybecker4.simulation.common.ui.NewtonianSimulator
import com.barrybecker4.simulation.trebuchet.model.RenderablePart
import com.barrybecker4.simulation.trebuchet.model.Trebuchet
import com.barrybecker4.ui.util.GUIUtil
import javax.swing._
import javax.swing.event.ChangeEvent
import javax.swing.event.ChangeListener
import java.awt._


/**
  * Physically base dynamic simulation of a trebuchet firing.
  * Try simulating using Breve.
  * @author Barry Becker
  */
object TrebuchetSimulator {
  private val DEFAULT_NUM_STEPS_PER_FRAME = 1
  // the amount to advance the animation in time for each frame in seconds
  private val TIME_STEP = 0.002
  private val BACKGROUND_COLOR = new Color(253, 250, 253)
  private val NUM_PARAMS = 3
}

class TrebuchetSimulator() extends NewtonianSimulator("Trebuchet") with ChangeListener {
  reset()
  this.setPreferredSize(new Dimension(800, 900))
  private var trebuchet: Trebuchet = _
  private var zoomSlider: JSlider = _

  def this(trebuchet: Trebuchet) {
    this()
    commonInit(trebuchet)
  }

  private def commonInit(trebuchet: Trebuchet): Unit = {
    this.trebuchet = trebuchet
    setNumStepsPerFrame(TrebuchetSimulator.DEFAULT_NUM_STEPS_PER_FRAME)
    this.setBackground(TrebuchetSimulator.BACKGROUND_COLOR)
    initCommonUI()
    this.render()
  }

  override protected def reset(): Unit = {
    val trebuchet = new Trebuchet
    commonInit(trebuchet)
  }

  override def getBackground: Color = TrebuchetSimulator.BACKGROUND_COLOR

  override def createTopControls: JPanel = {
    val controls = super.createTopControls
    val zoomPanel = new JPanel
    zoomPanel.setLayout(new FlowLayout)
    val zoomLabel = new JLabel(" Zoom")
    zoomSlider = new JSlider(SwingConstants.HORIZONTAL, 15, 255, 200)
    zoomSlider.addChangeListener(this)
    zoomPanel.add(zoomLabel)
    zoomPanel.add(zoomSlider)
    this.add(zoomPanel)
    controls.add(zoomLabel)
    controls.add(zoomSlider)
    controls
  }

  override def doOptimization(): Unit = {
    var optimizer: Optimizer = null
    if (GUIUtil.hasBasicService) optimizer = new Optimizer(this)
    else optimizer = new Optimizer(this, FileUtil.getHomeDir + "performance/trebuchet/trebuchet_optimization.txt")
    val params = new Array[Parameter](TrebuchetSimulator.NUM_PARAMS)
    //params[0] = new Parameter( WAVE_SPEED, 0.0001, 0.02, "wave speed" );
    //params[1] = new Parameter( WAVE_AMPLITUDE, 0.001, 0.2, "wave amplitude" );
    //params[2] = new Parameter( WAVE_PERIOD, 0.5, 9.0, "wave period" );
    val paramArray = new NumericParameterArray(params)
    setPaused(false)
    optimizer.doOptimization(OptimizationStrategyType.GENETIC_SEARCH, paramArray, 0.3)
  }

  def getNumParameters: Int = TrebuchetSimulator.NUM_PARAMS

  override protected def getInitialTimeStep: Double = TrebuchetSimulator.TIME_STEP

  override protected def createOptionsDialog = new TrebuchetOptionsDialog(frame, this)

  override def timeStep: Double = {
    if (!isPaused) timeStep_ = trebuchet.stepForward(timeStep_)
    timeStep_
  }

  override def paint(g: Graphics): Unit = {
    if (g == null) return
    val g2 = g.asInstanceOf[Graphics2D]
    g2.setColor(TrebuchetSimulator.BACKGROUND_COLOR)
    val dim = getSize()
    g2.fillRect(0, 0, dim.width, dim.height)
    val aliasing = if (getAntialiasing) RenderingHints.VALUE_ANTIALIAS_ON
                   else RenderingHints.VALUE_ANTIALIAS_OFF
    g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, aliasing)
    // draw the trebuchet in its current position
    trebuchet.render(g2)
  }

  override def setScale(scale: Double): Unit = {
    trebuchet.setScale(scale)
  }

  override def getScale: Double = trebuchet.getScale

  override def setShowVelocityVectors(show: Boolean): Unit = {
    RenderablePart.setShowVelocityVectors(show)
  }

  override def getShowVelocityVectors: Boolean = RenderablePart.getShowVelocityVectors

  override def setShowForceVectors(show: Boolean): Unit = {
    RenderablePart.setShowForceVectors(show)
  }

  override def getShowForceVectors: Boolean = RenderablePart.getShowForceVectors

  override def setDrawMesh(use: Boolean): Unit = {
    //trebuchet_.setDrawMesh(use);
  }

  override def getDrawMesh: Boolean = { //return trebuchet_.getDrawMesh();
    false
  }

  override def setStaticFriction(staticFriction: Double): Unit = {}

  override def getStaticFriction = 0.1
  override def setDynamicFriction(dynamicFriction: Double): Unit = {}

  override def getDynamicFriction = 0.01

  /** api for setting trebuchet params  */
  def getTrebuchet: Trebuchet = trebuchet

  override def stateChanged(event: ChangeEvent): Unit = {
    val src = event.getSource
    if (src eq zoomSlider) {
      val v = zoomSlider.getValue.toDouble / 200.0
      trebuchet.setScale(v)
      this.repaint()
    }
  }

  /**
    * Evaluates the trebuchet's fitness.
    * This method is an implement the Optimizee interface.
    * The measure is purely based on its velocity.
    * If the trebuchet becomes unstable, then 0.0 is returned.
    */
  override def evaluateFitness(params: ParameterArray): Double = {
    val stable = true
    val improved = true
    val oldVelocity = 0.0
    var ct = 0
    while ( {
      stable && improved
    }) { // let it run for a while
      ThreadUtil.sleep(1000 + (3000 / (1.0 + 0.2 * ct)).toInt)
      ct += 1
      //stable = trebuchet_.isStable();
    }
    if (!stable) {
      System.out.println("Trebuchet Sim unstable")
      10000.0
    }
    else 1.0 / oldVelocity
  }
}