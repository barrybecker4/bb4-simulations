/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.reactiondiffusion

import com.barrybecker4.ui.util.ColorMap
import com.barrybecker4.simulation.reactiondiffusion.algorithm.GrayScottController
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDColorMap
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDOffscreenRenderer
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDOnscreenRenderer
import com.barrybecker4.simulation.reactiondiffusion.rendering.RDRenderingOptions
import java.awt._


/**
  * Reaction diffusion viewer.
  */
object RDViewer {
  private val FIXED_SIZE_DIM = 250
}

class RDViewer private[reactiondiffusion](var grayScott: GrayScottController, var parent: Container) {
  oldWidth = this.parent.getWidth
  oldHeight = this.parent.getHeight
  private var cmap = new RDColorMap
  private var renderOptions = new RDRenderingOptions
  private var onScreenRenderer: RDOnscreenRenderer = _
  private var offScreenRenderer: RDOffscreenRenderer = _
  private var useFixedSize: Boolean = false
  private var useOfflineRendering = false
  private var oldWidth = 0
  private var oldHeight = 0

  private[reactiondiffusion] def getRenderingOptions = renderOptions

  /**
    * @param fixed if true then the render area does not resize automatically.
    */
  def setUseFixedSize(fixed: Boolean) { useFixedSize = fixed }
  def getUseFixedSize: Boolean = useFixedSize

  def setUseOffscreenRendering(use: Boolean) {useOfflineRendering = use}
  def getUseOffScreenRendering: Boolean = useOfflineRendering

  def getColorMap: ColorMap = cmap

  def paint(g: Graphics) {
    checkDimensions()
    val g2 = g.asInstanceOf[Graphics2D]
    getRenderer.render(g2)
  }

  /**
    * Sets to new size if needed.
    */
  private def checkDimensions() = {
    var w = RDViewer.FIXED_SIZE_DIM
    var h = RDViewer.FIXED_SIZE_DIM
    if (!useFixedSize) {
      w = parent.getWidth
      h = parent.getHeight
    }
    initRenderers(w, h)
  }

  private def initRenderers(w: Int, h: Int) = {
    if (w != oldWidth || h != oldHeight) {
      grayScott.setSize(w, h)
      onScreenRenderer = null
      offScreenRenderer = null
      oldWidth = w
      oldHeight = h
    }
  }

  private def getOffScreenRenderer = {
    if (offScreenRenderer == null)
      offScreenRenderer = new RDOffscreenRenderer(grayScott.getModel, cmap, renderOptions, parent)
    offScreenRenderer
  }

  private def getOnScreenRenderer = {
    if (onScreenRenderer == null)
      onScreenRenderer = new RDOnscreenRenderer(grayScott.getModel, cmap, renderOptions)
    onScreenRenderer
  }

  private def getRenderer = if (useOfflineRendering) getOffScreenRenderer else getOnScreenRenderer
}
