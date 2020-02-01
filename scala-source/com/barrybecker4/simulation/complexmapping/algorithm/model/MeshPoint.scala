/*
 * Copyright by Barry G. Becker, 2019. Licensed under MIT License: http://www.opensource.org/licenses/MIT
 */

package com.barrybecker4.simulation.complexmapping.algorithm.model

import com.barrybecker4.math.ComplexNumber
import com.barrybecker4.simulation.complexmapping.algorithm.functions.ComplexFunction
import javax.vecmath.Point2d

case class MeshPoint(pt: Point2d, value: Double) {

  def this(x: Double, y: Double, value: Double) {
    this(new Point2d(x, y), value)
  }

  def x: Double = pt.x
  def y: Double = pt.y

  def transform(func: ComplexFunction, n: Int): MeshPoint = {
    val transformed = func.compute(ComplexNumber(pt.x, pt.y), n)
    MeshPoint(new Point2d(transformed.real, transformed.imaginary), value)
  }

  def +(otherMeshPt: MeshPoint): MeshPoint =
    MeshPoint(new Point2d(pt.x + otherMeshPt.x, pt.y + otherMeshPt.y), value)

  def -(otherMeshPt: MeshPoint): MeshPoint =
    MeshPoint(new Point2d(pt.x - otherMeshPt.x, pt.y - otherMeshPt.y), value)

  def scale(s: Double): MeshPoint =
    MeshPoint(new Point2d(s * pt.x , s * pt.y), value)

}
