package com.barrybecker4.simulation.conway.model.kernal;

/**
 * Uses a kernal of some sort to count neighbors in a 2D grid.
 * @author Barry Becker
 */
public interface Kernel {

    double countNeighbors(int x, int y);

}
