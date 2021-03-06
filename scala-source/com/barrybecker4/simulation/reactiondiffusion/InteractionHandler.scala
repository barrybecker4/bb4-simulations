// Copyright by Barry G. Becker, 2016-2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.reactiondiffusion

import java.awt.event.{MouseEvent, MouseListener, MouseMotionListener}

import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottModel
import InteractionHandler.SQRT2

object InteractionHandler {
  val SQRT2: Double = Math.sqrt(2)
}

/**
  * Handle mouse interactions - converting them in to physical manifestations.
  * Using this handler, you can add more chemicals to the reaction.
  * Drag with left click to add U chemical locally.
  * Drag with right click to add V chemical locally.
  * @author Barry Becker
  */
class InteractionHandler(var model: GrayScottModel, var scale: Double)
  extends MouseListener with MouseMotionListener {

  private var currentX = 0
  private var currentY = 0
  private var brushRadius = GrayScottModel.DEFAULT_BRUSH_RADIUS
  private var brushStrength = GrayScottModel.DEFAULT_BRUSH_STRENGTH
  private var lastX = 0
  private var lastY = 0
  private var mouse1Down = false
  private var mouse3Down = false

  def setScale(scale: Double): Unit = {this.scale = scale }
  def setBrushRadius(rad: Int): Unit = { brushRadius = rad}
  def setBrushStrength(strength: Double): Unit = {brushStrength = strength}

  /** adds chemical U or V depending on the button pressed. */
  override def mouseDragged(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    doBrush()
    lastX = currentX
    lastY = currentY
  }

  private def doBrush(): Unit = {
    val i = (currentX / scale).toInt
    val j = (currentY / scale).toInt
    // apply the change to a convolution kernel area
    val startX = Math.max(1, i - brushRadius)
    val stopX = Math.min(model.getWidth - 1, i + brushRadius)
    val startY = Math.max(1, j - brushRadius)
    val stopY = Math.min(model.getHeight - 1, j + brushRadius)
    // adjust by this so that there is not a discontinuity at the periphery
    val minWt = 0.9 / (SQRT2 * brushRadius )
    for (ii <- startX to stopX)
      for (jj <- startY to stopY) {
        val weight = getWeight(i, j, ii, jj, minWt)
        applyChange(ii, jj, weight)
      }
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
  private def applyChange(i: Int, j: Int, weight: Double): Unit = {
    val amountToAdd = brushStrength * weight
    //println("m1 = " + mouse1Down + " m3 = " + mouse3Down + " amountToAdd " + amountToAdd +
    // " at " + i + " " + j + " curY=" + model.u(i)(j) + " curV=" + model.v(i)(j))
    // if the left mouse is down, make waves
    if (mouse1Down) model.u(i)(j) += amountToAdd
    else if (mouse3Down) model.v(i)(j) += amountToAdd
  }

  override def mouseMoved(e: MouseEvent): Unit = {
    currentX = e.getX
    currentY = e.getY
    lastX = currentX
    lastY = currentY
  }

  /** The following methods implement MouseListener */
  override def mouseClicked(e: MouseEvent): Unit = { doBrush() }

  /** Remember the mouse button that is pressed. */
  override def mousePressed(e: MouseEvent): Unit = {
    mouse1Down = e.getButton == MouseEvent.BUTTON1
    mouse3Down = e.getButton == MouseEvent.BUTTON3
  }

  override def mouseReleased(e: MouseEvent): Unit = {}
  override def mouseEntered(e: MouseEvent): Unit = {}
  override def mouseExited(e: MouseEvent): Unit = {}
}
