/** Copyright by Barry G. Becker, 2000-2017. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.cave

import com.barrybecker4.simulation.cave.model.CaveModel
import java.awt.event.MouseEvent
import java.awt.event.MouseListener
import java.awt.event.MouseMotionListener


/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * Using this handler, you can lower the gave walls.
  * @author Barry Becker
  */
class InteractionHandler(var cave: CaveModel, var scale: Double)
  extends MouseListener with MouseMotionListener {

  private var currentX = 0
  private var currentY = 0
  private var brushRadius = CaveModel.DEFAULT_BRUSH_RADIUS
  private var brushStrength = CaveModel.DEFAULT_BRUSH_STRENGTH
  private var lastX = 0
  private var lastY = 0
  private var mouse1Down = false
  private var mouse3Down = false

  def setScale(scale: Double) {this.scale = scale }
  def setBrushRadius(rad: Int){ brushRadius = rad}
  def setBrushStrength(strength: Double) {brushStrength = strength}

  /** Lowers (or raises) cave walls when dragging. Left mouse lowers; right mouse drag raises. */
  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    doBrush()
    lastX = currentX
    lastY = currentY
  }

  private def doBrush() = {
    val i = (currentX / scale).toInt
    val j = (currentY / scale).toInt
    // apply the change to a convolution kernel area
    val startX = Math.max(1, i - brushRadius)
    val stopX = Math.min(cave.getWidth, i + brushRadius)
    val startY = Math.max(1, j - brushRadius)
    val stopY = Math.min(cave.getHeight, j + brushRadius)
    // adjust by this so that there is not a discontinuity at the periphery
    val minWt = 0.9 / brushRadius
    for (ii <- startX until stopX) {
      for (jj <- startY until stopY) {
        val weight = getWeight(i, j, ii, jj, minWt)
        applyChange(ii, jj, weight)
      }
    }
    cave.doRender()
  }

  /** @return the weight is 1 / distance. */
  private def getWeight(i: Int, j: Int, ii: Int, jj: Int, minWt: Double) = {
    val deltaX = i.toDouble - ii
    val deltaY = j.toDouble - jj
    var distance = Math.sqrt(deltaX * deltaX + deltaY * deltaY)
    if (distance < 0.5) distance = 1.0
    1.0 / distance - minWt
  }

  /** Make waves or adds ink depending on which mouse key is being held down. */
  private def applyChange(i: Int, j: Int, weight: Double) = {
    var sign = 1
    // if the left mouse is down, make waves
    if (mouse1Down) {
    }
    else if (mouse3Down) sign = -1
    else // drag with no mouse click
    cave.incrementHeight(i, j, sign * brushStrength * weight)
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    lastX = currentX
    lastY = currentY
  }

  /** The following methods implement MouseListener */
  override def mouseClicked(e: MouseEvent) {doBrush() }

  /** Remember the mouse button that is pressed. */
  override def mousePressed(e: MouseEvent): Unit = {
    mouse1Down = (e.getModifiers & MouseEvent.BUTTON1) == MouseEvent.BUTTON1
    mouse3Down = (e.getModifiers & MouseEvent.BUTTON3) == MouseEvent.BUTTON3
  }

  override def mouseReleased(e: MouseEvent) {}
  override def mouseEntered(e: MouseEvent) {}
  override def mouseExited(e: MouseEvent){}
}
