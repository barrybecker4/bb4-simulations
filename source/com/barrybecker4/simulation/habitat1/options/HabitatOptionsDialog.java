/** Copyright by Barry G. Becker, 2000-2011. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.habitat1.options;

import com.barrybecker4.simulation.common1.ui.Simulator;
import com.barrybecker4.simulation.common1.ui.SimulatorOptionsDialog;
import com.barrybecker4.simulation.habitat1.HabitatSimulator;

import javax.swing.*;
import java.awt.*;

/**
 * @author Barry Becker
 */
public class HabitatOptionsDialog extends SimulatorOptionsDialog {

    public HabitatOptionsDialog(Component parent, Simulator simulator) {
        super(parent, simulator);
    }

    @Override
    protected JPanel createCustomParamPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));

        HabitatSimulator sim = (HabitatSimulator) getSimulator();

        return panel;
    }
}