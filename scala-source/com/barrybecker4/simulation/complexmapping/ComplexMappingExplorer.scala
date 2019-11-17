/* Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.complexmapping

import java.awt.Graphics
import com.barrybecker4.simulation.common.ui.Simulator
import com.barrybecker4.simulation.complexmapping.algorithm.{FunctionType, MeshColorMap}
import com.barrybecker4.simulation.complexmapping.algorithm.functions.{ComplexFunction, DerichletEtaFunction, IdentityFunction}
import com.barrybecker4.ui.util.ColorMap
import javax.swing.JPanel
import javax.vecmath.Point2d
import ComplexMappingExplorer._
import com.barrybecker4.simulation.complexmapping.algorithm.model.{Box, Grid, MeshMappingModel}


object ComplexMappingExplorer {
  val DEFAULT_VIEWPORT: Box = Box(new Point2d(-2, 4), new Point2d(5, -4))
  val DEFAULT_FUNCTION: FunctionType.Val = FunctionType.RIEMANN_ZETA
  val DEFAULT_INTERPOLATION_VAL: Double = 1.0
  val DEFAULT_MESH_DETAIL: Double = 0.1
}

/**
  * Interactively explores what happens when a specified function is applied to a grid of points in the complex plane.
  * Ideas for improvement:
  *  - use editable Box coordinates to specity rectangular region mapped.
  *  - dropdown for color function, slider for scaling color
  *  - dropdown for complex mapping function and UI for its options
  *  - figure out how to analytically extend zeta function.
  * @author Barry Becker.
  */
class ComplexMappingExplorer extends Simulator("Complex Mapping Explorer") {

  private var function: ComplexFunction = DEFAULT_FUNCTION.function
  private var origGridBounds = Box(new Point2d(1.0, 3.0), new Point2d(3.0, -3.0))
  private var increment = DEFAULT_MESH_DETAIL
  private var grid = new Grid(origGridBounds, increment, increment)
  private var model: MeshMappingModel = MeshMappingModel(grid, function, DEFAULT_INTERPOLATION_VAL)
  private var options: DynamicOptions = _
  private var useFixedSize: Boolean = false
  private var viewport: Box = DEFAULT_VIEWPORT
  private var interpolationValue = DEFAULT_INTERPOLATION_VAL
  private var colorMap: ColorMap = new MeshColorMap()

  commonInit()

  /** @param fixed if true then the render area does not resize automatically.*/
  def setUseFixedSize(fixed: Boolean): Unit = {
    useFixedSize = fixed
  }

  def getUseFixedSize: Boolean = useFixedSize

  private def commonInit(): Unit = {
    initCommonUI()
    redraw()
  }

  def setFunction(func: ComplexFunction): Unit = {
    function = func
    redraw()
  }

  def setOriginalGridBounds(bounds: Box): Unit = {
    origGridBounds = bounds
    grid = new Grid(origGridBounds, increment, increment)
    redraw()
  }

  def setInterpolation(v: Double): Unit = {
    interpolationValue = v
    redraw()
  }

  def setMeshDetailIncrement(inc: Double): Unit = {
    increment = inc
    grid = new Grid(origGridBounds, increment, increment)
    redraw()
  }

  /** @return the current algorithm. Note: it can change so do not hang onto a reference. */
  def getFunction: ComplexFunction = function

  override protected def reset(): Unit = {
    if (options != null) options.reset()
    redraw()
  }

  def redraw(): Unit = {
    model = MeshMappingModel(grid, function, interpolationValue, colorMap)
    this.repaint()
  }

  override protected def createOptionsDialog = new ComplexMappingOptionsDialog(frame, this)
  override protected def getInitialTimeStep: Double = 1
  override def timeStep: Double = tStep

  override def paint(g: Graphics): Unit = {
    super.paint(g)
    if (g != null) {
      g.drawImage(model.getImage(this.getWidth, this.getHeight), 0, 0, null)
    }
  }


  override def setScale(scale: Double): Unit = {}
  override def getScale = 0.01

  override def createDynamicControls: JPanel = {
    options = new DynamicOptions(this)
    options
  }

  def getColorMap: ColorMap = model.getColorMap
}
