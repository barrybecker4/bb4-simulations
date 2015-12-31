package com.barrybecker4.simulation.cave.model;

import com.barrybecker4.common.math.Range;
import com.barrybecker4.simulation.cave.model.kernal.BasicKernel;
import com.barrybecker4.simulation.cave.model.kernal.Kernel;
import com.barrybecker4.simulation.cave.model.kernal.RadialKernel;

/**
 * This Cave simulation program is based on work by Michael Cook
 * See http://gamedevelopment.tutsplus.com/tutorials/generate-random-cave-levels-using-cellular-automata--gamedev-9664
 * See http://www.roguebasin.com/index.php?title=Cellular_Automata_Method_for_Generating_Random_Cave-Like_Levels
 * @author Brian Becker
 * @author Barry Becker
 */
public class CaveProcessor {

    /** The density is the chance that a cell starts as part of the cave area (alive) */
    public static final double DEFAULT_FLOOR_THRESH = .2;
    public static final double DEFAULT_CEIL_THRESH = .8;
    public static final int DEFAULT_HEIGHT = 32;
    public static final int DEFAULT_WIDTH = 32;

    /** cells die if less than this */
    public static final double DEFAULT_LOSS_FACTOR = 0.5;

    /** Cells are born if more than this many neighbors */
    public static final double DEFAULT_EFFECT_FACTOR = 0.2;

    public enum KernelType {BASIC, RADIAL}
    public static final KernelType DEFAULT_KERNEL_TYPE = KernelType.BASIC;

    private double lossFactor;
    private double effectFactor;
    private Cave cave;
    private Kernel kernel;

    /** Default no argument constructor */
    public CaveProcessor() {
        this(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /** Constructor that allows you to specify the dimensions of the cave */
    public CaveProcessor(int width, int height) {
        this(width, height,
           DEFAULT_FLOOR_THRESH, DEFAULT_CEIL_THRESH, DEFAULT_LOSS_FACTOR, DEFAULT_EFFECT_FACTOR, KernelType.BASIC);
    }

    public CaveProcessor(int width, int height,
              double floorThresh, double ceilThresh, double lossFactor, double effectFactor, KernelType kernelType) {
        this.lossFactor = lossFactor;
        this.effectFactor = effectFactor;
        cave = new Cave(width, height, floorThresh, ceilThresh);
        setKernelType(kernelType);

    }

    public int getWidth() {
        return cave.getWidth();
    }

    public int getHeight() {
        return cave.getLength();
    }

    public void setKernelType(KernelType type) {
        switch (type) {
            case BASIC: kernel = new BasicKernel(cave); break;
            case RADIAL: kernel = new RadialKernel(cave); break;
        }
    }

    public void setLossFactor(double loss) {
        lossFactor = loss;
    }

    public void setEffectFactor(double scale) {
        effectFactor = scale;
    }

    public void setFloorThresh(double floor) {
       cave.setFloorThresh(floor);
    }

    public void setCeilThresh(double ceil) {
       cave.setCeilThresh(ceil);
    }
    /**
     * Compute the next step of the simulation
     * The new value is at each point based on simulation rules:
     * - if a cell is alive but has too few neighbors, kill it.
     * - otherwise, if the cell is dead now, check if it has the right number of neighbors to be 'born'
     */
    public void nextPhase() {
        Cave newCave = cave.createCopy();
        // Loop over each row and column of the map
        for (int x = 0; x < cave.getWidth(); x++) {
            for (int y = 0; y < cave.getLength(); y++) {
                double neibNum = kernel.countNeighbors(x, y);
                double oldValue = cave.getValue(x, y);
                double newValue = oldValue;
                newValue = oldValue + (neibNum - lossFactor) * effectFactor;
                newCave.setValue(x, y, newValue);
            }
        }
        cave = newCave;
    }

    public double getValue (int x, int y) {
        return cave.getValue(x, y);
    }

    public Range getRange() {
        return cave.getRange();
    }

    public void printCave() {
        cave.print();
    }

    public String toString() {
        return cave.toString();
    }

    public static void main(String[] args) {
        CaveProcessor cave = new CaveProcessor(32, 32, 0.25, 0.8, 3, 2, KernelType.BASIC);
        cave.printCave();
        cave.nextPhase();
        cave.printCave();
    }

}
