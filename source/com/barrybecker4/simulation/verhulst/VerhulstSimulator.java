/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.verhulst;

import com.barrybecker4.common.math.function.CountFunction;
import com.barrybecker4.common.math.function.Function;
import com.barrybecker4.simulation.common.ui.Simulator;
import com.barrybecker4.simulation.common.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.graphing.GraphOptionsDialog;
import com.barrybecker4.ui.animation.AnimationFrame;
import com.barrybecker4.ui.renderers.MultipleFunctionRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Simulates foxes (predators) and rabbits (prey) in the wild.
 *
 * @author Barry Becker
 */
public class VerhulstSimulator extends Simulator {

    MultipleFunctionRenderer graph_;
    long iteration;

    Rabbits rabbits;
    CountFunction rabbitFunction;
    DynamicOptions options_;

    /** Constructor */
    public VerhulstSimulator() {
        super("Verhulst Simulation");
        rabbits = new Rabbits();
        initGraph();
    }

    public List<Population> getCreatures() {
        List<Population> creatures = new ArrayList<Population>();
        creatures.add(rabbits);
        return creatures;
    }

    @Override
    protected void reset() {

        initGraph();
        options_.reset();
    }

    @Override
    protected double getInitialTimeStep() {
        return 1.0;
    }

    @Override
    public double timeStep() {
        iteration++;
        double newPop =
            rabbits.getPopulation() * ( (1.0 + rabbits.birthRate) - rabbits.birthRate * rabbits.getPopulation());

        //System.out.println("pop="+ newPop + " rate="+ rabbits.birthRate);
        rabbits.setPopulation(newPop);

        rabbitFunction.addValue(iteration, rabbits.getPopulation());
        return timeStep_;
    }

    protected void initGraph() {
        iteration = 0;

        rabbits.reset();
        rabbitFunction = new CountFunction(Rabbits.INITIAL_NUM_RABBITS);
        rabbitFunction.setMaxXValues(200);

        List<Function> functions = new LinkedList<Function>();
        functions.add(rabbitFunction);

        List<Color> lineColors = new LinkedList<Color>();
        lineColors.add(Rabbits.COLOR);

        graph_ = new MultipleFunctionRenderer(functions, lineColors);
    }

    @Override
    public JPanel createDynamicControls() {
        options_ = new DynamicOptions(this);
        return options_;
    }

    @Override
    protected SimulatorOptionsDialog createOptionsDialog() {
        return new GraphOptionsDialog(frame, this );
    }

    @Override
    public void paint( Graphics g ) {
        graph_.setSize(getWidth(), getHeight());
        graph_.paint(g);
    }

    public static void main( String[] args ) {
        final VerhulstSimulator sim = new VerhulstSimulator();

        sim.setPaused(true);
        new AnimationFrame( sim );
    }
}