// Copyright by Barry G. Becker, 2013. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave.model;

import com.barrybecker4.simulation.cave.rendering.CaveColorMap;
import com.barrybecker4.simulation.cave.rendering.CaveRenderer;
import com.barrybecker4.simulation.common.Profiler;
import com.barrybecker4.ui.util.ColorMap;

import javax.swing.*;
import java.awt.image.BufferedImage;

import static com.barrybecker4.simulation.cave.model.CaveProcessor.*;

/**
 * Communicates changing dynamic options to ConwayProcessor and controls the rendering of the cave.
 * @author Barry Becker
 */
public class CaveModel {

    public static final int DEFAULT_SCALE_FACTOR = 2;
    public static final double DEFAULT_BUMP_HEIGHT = 0.0;
    public static final double DEFAULT_SPECULAR_PCT = 0.1;
    public static final double DEFAULT_LIGHT_SOURCE_ELEVATION = Math.PI / 4.0;
    public static final double DEFAULT_LIGHT_SOURCE_AZYMUTH = Math.PI / 4.0;
    public static final boolean DEFAULT_USE_CONTINUOUS_ITERATION = false;
    public static final int DEFAULT_NUM_STEPS_PER_FRAME = 1;

    /** Radius for area of effect when doing manual modification with click/drag brush */
    public static final int DEFAULT_BRUSH_RADIUS = 8;

    /** The amount of impact that the brush has. */
    public static final double DEFAULT_BRUSH_STRENGTH = 0.6;

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
    private int numStepsPerFrame = DEFAULT_NUM_STEPS_PER_FRAME;
    private boolean useParallel = DEFAULT_USE_PARALLEL;

    private int numIterations = 0;
    private double bumpHeight = DEFAULT_BUMP_HEIGHT;
    private double specularPct = DEFAULT_SPECULAR_PCT;
    private double lightSourceDescensionAngle = DEFAULT_LIGHT_SOURCE_ELEVATION;
    private double lightSourceAzymuthAngle = DEFAULT_LIGHT_SOURCE_AZYMUTH;
    private boolean restartRequested = false;
    private boolean nextStepRequested = false;
    private boolean continuousIteration = DEFAULT_USE_CONTINUOUS_ITERATION;
    private ColorMap cmap = new CaveColorMap();


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
                floorThresh, ceilThresh, lossFactor, effectFactor, kernalType, useParallel);
        renderer = new CaveRenderer(DEFAULT_WIDTH, DEFAULT_HEIGHT, cave, cmap);
    }

    public int getWidth() {
        return cave.getWidth();
    }

    public int getHeight() {
        return cave.getHeight();
    }

    public void setFloorThresh(double floor) {
        cave.setFloorThresh(floor);
        this.floorThresh = floor;
        doRender();
    }

    public void setCeilThresh(double ceil) {
        cave.setCeilThresh(ceil);
        this.ceilThresh = ceil;
        doRender();
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
        doRender();
    }

    public void setSpecularPercent(double pct) {
        this.specularPct = pct;
        doRender();
    }

    public void setDefaultUseContinuousIteration(boolean continuous) {
        this.continuousIteration = continuous;
        doRender();
    }

    public void setLightSourceDescensionAngle(double descensionAngle) {
        this.lightSourceDescensionAngle = descensionAngle;
        doRender();
    }

    public void setLightSourceAzymuthAngle(double azymuthAngle) {
        this.lightSourceAzymuthAngle = azymuthAngle;
        doRender();
    }

    public void setScale(double scale) {
        this.scale = scale;
        requestRestart(renderer.getWidth(), renderer.getHeight());
    }

    public double getScale() {
        return this.scale;
    }

    public void setNumStepsPerFrame(int steps) {
        this.numStepsPerFrame = steps;
    }

    public void setUseParallelComputation(boolean use) {
        useParallel = use;
        cave.setUseParallel(use);
    }

    public void incrementHeight(int x, int y, double amount) {
        cave.incrementHeight(x, y, amount);
    }

    public ColorMap getColormap() {
        return cmap;
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
    }

    public int getNumIterations() {
        return numIterations;
    }

    private void requestRestart(int width, int height) {
        try {
            int caveWidth = (int)(width / scale);
            int caveHeight = (int)(height / scale);
            cave = new CaveProcessor(caveWidth, caveHeight,
                    floorThresh, ceilThresh, lossFactor, effectFactor, kernalType, useParallel);
            numIterations = 0;
            renderer = new CaveRenderer(width, height, cave, cmap);
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
            doRender();
        }
        else if (nextStepRequested || continuousIteration) {
            for (int i = 0; i < numStepsPerFrame; i++)
            cave.nextPhase();
            numIterations++;
            doRender();
            nextStepRequested = false;
        }

        return false;
    }

    public void doRender() {
        renderer.render(bumpHeight, specularPct, lightSourceAzymuthAngle, lightSourceDescensionAngle);
    }
}
