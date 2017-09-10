// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.lsystem1.rendering;

import com.barrybecker4.common.geometry.IntLocation;

/**
 * The current position and orientation used while rendering.
 *
 * @author Barry Becker
 */
class OrientedPosition {
    double x;
    double y;
    double angle;

    /** constructor */
    OrientedPosition(double x, double y, double angle)
    {
        this.x = x;
        this.y = y;
        this.angle = angle;
    }

    /** copy constructor */
    OrientedPosition(OrientedPosition pos) {
        this(pos.x, pos.y, pos.angle);
    }

    IntLocation getLocation() {
        return new IntLocation((int)y, (int)x);
    }
}
