package com.barrybecker4.simulation.cave.model.kernal;

import com.barrybecker4.simulation.cave.model.Cave;

/**
 * Looks only at immediate neighbors
 * @author Barry Becker
 */
public class BasicKernel extends AbstractKernel {

    public BasicKernel(Cave cave) {
        super(cave);
    }

    public double countNeighbors(int x, int y) {

        int count = 0;
        for (int i = -1; i < 2; i++) {
            int neighborX = x + i;
            for (int j = -1; j < 2; j++) {
                int neighborY = y + j;
                // If we're looking at the middle point
                if (i == 0 && j == 0) {
                    // Do nothing, we don't want to add ourselves in!
                    continue;
                }
                // In case the index we're looking at it off the edge of the map, or a filled neighbor
                if (isNbr(neighborX, neighborY)) {
                    count++;
                }
            }
        }
        return count;
    }
}
