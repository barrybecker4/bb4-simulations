// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model;

import com.barrybecker4.simulation.cave.rendering.CaveRenderer;
import com.barrybecker4.simulation.common.Profiler;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static com.barrybecker4.simulation.cave.model.CaveProcessor.*;

/**
 * @author Barry Becker
 */
public class CaveModel {

    public static final double DEFAULT_SCALE_FACTOR = 5.0;
    public static final boolean DEFAULT_USE_BUMP_MAPPING = false;
    public static final double DEFAULT_BUMP_HEIGHT = 10;

    private static final int DEFAULT_WIDTH = 400;
    private static final int DEFAULT_HEIGHT = 400;

    private CaveProcessor cave;
    private CaveRenderer renderer;

    private double floorThresh = DEFAULT_FLOOR_THRESH;
    private double ceilThresh = DEFAULT_CEIL_THRESH;
    private double lossFactor = DEFAULT_LOSS_FACTOR;
    private double effectFactor = DEFAULT_EFFECT_FACTOR;
    private double scale = DEFAULT_SCALE_FACTOR;
    private CaveProcessor.KernelType kernalType = CaveProcessor.DEFAULT_KERNEL_TYPE;

    private int numIterations = 0;
    private boolean useBumpmapping = DEFAULT_USE_BUMP_MAPPING;
    private double bumpHeight = DEFAULT_BUMP_HEIGHT;
    private boolean restartRequested = false;
    private boolean nextStepRequested = false;


    public CaveModel() {
        reset();
    }

    public void setSize(int width, int height)  {
        if (width != renderer.getWidth() || height != renderer.getHeight())   {
            requestRestart(width, height);
        }
    }

    public void reset() {
        floorThresh = DEFAULT_FLOOR_THRESH;
        ceilThresh = DEFAULT_CEIL_THRESH;
        int caveWidth = (int)(DEFAULT_WIDTH / scale);
        int caveHeight = (int)(DEFAULT_HEIGHT / scale);
        CaveProcessor cave = new CaveProcessor(caveWidth, caveHeight,
                floorThresh, ceilThresh, lossFactor, effectFactor, kernalType);
        renderer = new CaveRenderer(DEFAULT_WIDTH, DEFAULT_HEIGHT, cave);
    }

    public void setFloorThresh(double floor) {
        cave.setFloorThresh(floor);
        this.floorThresh = floor;
    }

    public void setCeilThresh(double ceil) {
        cave.setCeilThresh(ceil);
        this.ceilThresh = ceil;
    }

    public void setLossFactor(double lossFactor) {
        cave.setLossFactor(lossFactor);
        this.lossFactor = lossFactor;
    }

    public void setEffectFactor(double effectFactor) {
        cave.setEffectFactor(effectFactor);
        this.effectFactor = effectFactor;
    }

    public void setBumpHeight(double ht) {
        this.bumpHeight = ht;
    }

    public void setUseBumpmapping(boolean useBumpmapping) {
        this.useBumpmapping = useBumpmapping;
    }

    public void setScale(double scale) {
        this.scale = scale;
    }

    public void requestRestart() {
        requestRestart(renderer.getWidth(), renderer.getHeight());
    }

    public void requestNextStep() {
        nextStepRequested = true;
    }

    public void setKernelType(CaveProcessor.KernelType type) {
        cave.setKernelType(type);
        this.kernalType = type;
        //requestRestart(renderer.getWidth(), renderer.getHeight());
    }

    private void requestRestart(int width, int height) {
        try {
            int caveWidth = (int)(width / scale);
            int caveHeight = (int)(height / scale);
            cave = new CaveProcessor(caveWidth, caveHeight,
                    floorThresh, ceilThresh, lossFactor, effectFactor, kernalType);
            numIterations = 0;
            renderer = new CaveRenderer(width, height, cave);
            restartRequested = true;
        } catch (IllegalArgumentException e) {
            JOptionPane.showMessageDialog(null, e.getMessage());
        }
    }

    public BufferedImage getImage() {
        return renderer.getImage();
    }

    /**
     * @param timeStep number of rows to compute on this timestep.
     * @return true when done computing whole renderer.
     */
    public boolean timeStep(double timeStep) {

        if (restartRequested) {
            restartRequested = false;
            nextStepRequested = false;
            numIterations = 0;
            Profiler.getInstance().startCalculationTime();
            renderer.render(useBumpmapping, bumpHeight);
        }
        else if (nextStepRequested) {
            cave.nextPhase();
            numIterations++;
            renderer.render(useBumpmapping, bumpHeight);
            nextStepRequested = false;
        }

        return false;
    }
}
