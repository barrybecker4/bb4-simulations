package com.barrybecker4.simulation.conway.model;

import com.barrybecker4.common.math.Range;

import java.util.Random;

/**
 * @author Barry Becker
 */
public class Conway {

    private static final int SEED = 0;
    private static final Random RAND = new Random();

    /** a value representing the height. MAX_HEIGHT is wall, MIN_HEIGHT is floor  */
    private double[][] heightMap;
    private double floorThresh = 0.2;
    private double ceilThresh = 0.9;

    public Conway(int width, int length, double floorThresh, double ceilThresh) {
        this.floorThresh = floorThresh;
        this.ceilThresh = ceilThresh;
        heightMap = genMap(width, length);
    }

    public int getWidth() {
        return heightMap.length;
    }
    public int getLength() {
        return heightMap[0].length;
    }

    public Range getRange() {
        return new Range(floorThresh, ceilThresh);
    }
    public void setValue(int x, int y, double value) {
        heightMap[x][y] = Math.min(Math.max(value, floorThresh), ceilThresh);
    }

    public Conway createCopy() {
        Conway newCave = new Conway(getWidth(), getLength(), this.floorThresh, this.ceilThresh);
        for (int x = 0; x < getWidth(); x++) {
            System.arraycopy(heightMap[x], 0, newCave.heightMap[x], 0, getLength());
        }
        return newCave;
    }

    public double getValue(int x, int y) {
        return heightMap[x][y];
    }

    /**
     * @param amount the amount to change the height by. Will never go above 1 or below 0.
     */
    public void incrementHeight(int x, int y, double amount) {
        double oldVal = heightMap[x][y];
        heightMap[x][y] = Math.max(floorThresh, Math.min(ceilThresh, oldVal + amount));
    }

    private char getChar(int x, int y) {
        double v = heightMap[x][y];
        if (v < floorThresh) return ' ';
        else if (v < ceilThresh) return 'C';
        else return 'W';
    }

    /** generate the initial random 2D typeMap data */
    private double[][] genMap(int width, int length) {
        RAND.setSeed(SEED);
        double[][] map = new double[width][length];

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < length; y++) {
                double r = RAND.nextDouble();
                map[x][y] = Math.min(Math.max(r, floorThresh), ceilThresh);
            }
        }
        return map;
    }

    public void print() {
        System.out.println(this.toString());
    }

    public String toString() {
        StringBuilder bldr = new StringBuilder();
        for (int y = 0; y < getLength(); y++) {
            for (int x = 0; x < getWidth(); x++) {
                bldr.append(getChar(x, y));
            }
            bldr.append("\n");
        }
        return bldr.toString();
    }
}
