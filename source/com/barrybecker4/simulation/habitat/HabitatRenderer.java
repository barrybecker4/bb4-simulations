/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat;

import com.barrybecker4.simulation.habitat.creatures.Creature;
import com.barrybecker4.simulation.habitat.creatures.Population;
import com.barrybecker4.simulation.habitat.creatures.Populations;

import java.awt.*;

/**
 * This class draws a the global hab and all the creatures in it.
 *
 * @author Barry Becker
 */
public class HabitatRenderer  {

    private static final double SIZE_SCALE = 0.001;
    private Populations populations;

    private int width;
    private int height;

    /**
     * Constructor.
     */
    HabitatRenderer(Populations populations) {
        this.populations = populations;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /** draw the cartesian functions */
    public void paint(Graphics g) {

        if (g == null)  return;
        Graphics2D g2 = (Graphics2D) g;

        for (Population pop : populations) {
            g2.setColor(pop.getType().getColor());
            for (Creature creature : pop.getCreatures())  {
                drawCreature(creature, g2);
            }
        }
    }

    private void drawCreature(Creature creature, Graphics2D g2) {
        int w = (int) (creature.getSize() * width * SIZE_SCALE + 1.0);
        int h = (int) (creature.getSize() * height * SIZE_SCALE + 1.0);
        int centerX = (int)(creature.getLocation().x * width);
        int centerY = (int)(creature.getLocation().y * height);
        g2.fillOval(centerX - w/2, centerY - h/2, w, h);

        int vectorEndpointX = (int)(centerX + creature.getVelocity().x * width);
        int vectorEndpointY = (int)(centerY + creature.getVelocity().y * height);
        g2.drawLine(centerX, centerY, vectorEndpointX, vectorEndpointY);

        if (creature.isPursuing()) {
            g2.drawOval(centerX - w, centerY - h, 2*w, 2*h);
        }
    }

}