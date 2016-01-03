// Copyright by Barry G. Becker, 2016. Licensed under MIT License: http://www.opensource.org/licenses/MIT
package com.barrybecker4.simulation.cave;

import com.barrybecker4.common.concurrency.ThreadUtil;
import com.barrybecker4.simulation.cave.model.CaveProcessor;
import com.barrybecker4.simulation.cave.model.CaveModel;
import com.barrybecker4.ui.legend.ContinuousColorLegend;
import com.barrybecker4.ui.sliders.SliderGroup;
import com.barrybecker4.ui.sliders.SliderGroupChangeListener;
import com.barrybecker4.ui.sliders.SliderProperties;
import com.barrybecker4.ui.util.ColorMap;

import javax.swing.*;
import javax.swing.border.EtchedBorder;
import java.awt.*;
import java.awt.event.*;

/**
 * Dynamic controls for the RD simulation that will show on the right.
 * They change the behavior of the simulation while it is running.
 * @author Barry Becker
 */
class DynamicOptions extends JPanel
                     implements SliderGroupChangeListener, ItemListener, ActionListener {

    private CaveModel caveModel;

    private Choice kernelChoice;
    private JButton nextButton;
    private JButton resetButton;

    private static final String FLOOR_SLIDER = "Floor";
    private static final String CEILING_SLIDER = "Ceiling";
    private static final String LOSS_FACTOR_SLIDER = "Loss Factor";
    private static final String BRUSH_RADIUS_SLIDER = "Brush radius";
    private static final String BRUSH_STRENGTH_SLIDER = "Brush strength";
    private static final String EFFECT_FACTOR_SLIDER = "Effect Factor";
    private static final String BUMP_HEIGHT_SLIDER = "Height (for bumps)";
    private static final String SPECULAR_PCT_SLIDER = "Specular Highlight (for bumps)";
    private static final String LIGHT_SOURCE_ELEVATION_SLIDER = "Light source elevation angle (for bumps)";
    private static final String LIGHT_SOURCE_AZYMUTH_SLIDER = "Light azymuthal angle (for bumps)";
    private static final String SCALE_SLIDER = "Scale";
    private static final double PI_D2 = Math.PI / 2.0;
    private static final int PREFERRED_WIDTH = 300;

    private SliderGroup generalSliderGroup_;
    private SliderGroup bumpSliderGroup_;
    private SliderGroup brushSliderGroup_;

    private JCheckBox useContinuousIteration_;
    private CaveExplorer simulator_;

    private static final SliderProperties[] GENERAL_SLIDER_PROPS = {

        new SliderProperties(FLOOR_SLIDER,   0,    1.0,    CaveProcessor.DEFAULT_FLOOR_THRESH, 100),
        new SliderProperties(CEILING_SLIDER,   0,    1.0,   CaveProcessor.DEFAULT_CEIL_THRESH, 100),
        new SliderProperties(LOSS_FACTOR_SLIDER,  0,   1.0,  CaveProcessor.DEFAULT_LOSS_FACTOR, 100),
        new SliderProperties(EFFECT_FACTOR_SLIDER,  0,   1.0,  CaveProcessor.DEFAULT_EFFECT_FACTOR, 100),
        new SliderProperties(SCALE_SLIDER,           1,   20,  CaveModel.DEFAULT_SCALE_FACTOR),
    };

    private static final SliderProperties[] BUMP_SLIDER_PROPS = {
        new SliderProperties(BUMP_HEIGHT_SLIDER,  0.0,   10.0,  CaveModel.DEFAULT_BUMP_HEIGHT, 100),
        new SliderProperties(SPECULAR_PCT_SLIDER,  0.0,   1.0,  CaveModel.DEFAULT_SPECULAR_PCT, 100),
        new SliderProperties(LIGHT_SOURCE_ELEVATION_SLIDER, 0.0, Math.PI/2.0,  CaveModel.DEFAULT_LIGHT_SOURCE_ELEVATION, 100),
        new SliderProperties(LIGHT_SOURCE_AZYMUTH_SLIDER, 0.0, Math.PI,  CaveModel.DEFAULT_LIGHT_SOURCE_AZYMUTH, 100),
    };

    private static final SliderProperties[] BRUSH_SLIDER_PROPS = {

        new SliderProperties(BRUSH_RADIUS_SLIDER,  1,   30,  CaveModel.DEFAULT_BRUSH_RADIUS),
        new SliderProperties(BRUSH_STRENGTH_SLIDER, 0.1,   1,  CaveModel.DEFAULT_BRUSH_STRENGTH, 100),
    };

    /**
     * Constructor
     */
    DynamicOptions(CaveModel algorithm, CaveExplorer simulator) {

        simulator_ = simulator;
        setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
        setBorder(BorderFactory.createEtchedBorder());
        setPreferredSize(new Dimension(PREFERRED_WIDTH, 850));

        caveModel = algorithm;

        JPanel generalPanel = createGeneralControls(algorithm.getColormap());
        JPanel bumpPanel = createBumpControls();
        JPanel brushPanel = createBrushControls();

        add(generalPanel);
        add(Box.createVerticalStrut(12));
        add(bumpPanel);
        add(Box.createVerticalStrut(12));
        add(brushPanel);

        JPanel fill = new JPanel();
        fill.setPreferredSize(new Dimension(1, 1000));
        add(fill);
    }

    private JPanel createGeneralControls(ColorMap cmap) {
        JPanel panel = new JPanel(new BorderLayout());
        final int southPanelHt = 150;
        int ht = GENERAL_SLIDER_PROPS.length * 50 + southPanelHt;
        panel.setPreferredSize(new Dimension(300, ht));
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        generalSliderGroup_ = new SliderGroup(GENERAL_SLIDER_PROPS);
        generalSliderGroup_.addSliderChangeListener(this);

        ContinuousColorLegend legend = new ContinuousColorLegend(null, cmap, true);

        JLabel title = new JLabel("General Cave Parameters");
        panel.add(title, BorderLayout.NORTH);
        panel.add(generalSliderGroup_, BorderLayout.CENTER);

        JPanel southPanel = new JPanel(new BorderLayout());
        southPanel.setPreferredSize(new Dimension(PREFERRED_WIDTH, southPanelHt));
        JPanel cp = new JPanel();

        cp.add(createKernalDropdown());
        cp.add(createIncrementPanel());
        cp.add(createButtons());
        southPanel.add(cp, BorderLayout.CENTER);
        southPanel.add(legend, BorderLayout.SOUTH);

        panel.add(southPanel, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel createBumpControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        bumpSliderGroup_ = new SliderGroup(BUMP_SLIDER_PROPS);
        bumpSliderGroup_.addSliderChangeListener(this);

        JLabel title = new JLabel("Bump Parameters");
        panel.add(title, BorderLayout.NORTH);
        panel.add(bumpSliderGroup_, BorderLayout.CENTER);
        return panel;
    }

    private JPanel createBrushControls() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBorder(BorderFactory.createEtchedBorder(EtchedBorder.RAISED));

        brushSliderGroup_ = new SliderGroup(BRUSH_SLIDER_PROPS);
        brushSliderGroup_.addSliderChangeListener(this);

        JLabel title = new JLabel("Brush Parameters (left: raise; right: lower)");
        panel.add(title, BorderLayout.NORTH);
        panel.add(brushSliderGroup_, BorderLayout.CENTER);
        return panel;
    }

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createKernalDropdown() {

        JPanel kernelChoicePanel = new JPanel();
        JLabel label = new JLabel("Kernal type: ");

        kernelChoice = new Choice();
        for (Enum kernelType: CaveProcessor.KernelType.values()) {
            kernelChoice.add(kernelType.name());
        }
        kernelChoice.select(CaveProcessor.DEFAULT_KERNEL_TYPE.ordinal());
        kernelChoice.addItemListener(this);

        kernelChoicePanel.add(label);
        kernelChoicePanel.add(kernelChoice);
        return kernelChoicePanel;
    }

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createIncrementPanel() {
        JPanel panel = new JPanel();

        JLabel label = new JLabel("Continuous iteration: ");
        useContinuousIteration_ = new JCheckBox();
        useContinuousIteration_.setSelected(CaveModel.DEFAULT_USE_CONTINUOUS_ITERATION);
        useContinuousIteration_.addActionListener(this);

        nextButton = new JButton("Next");
        nextButton.addActionListener(this);
        nextButton.setEnabled(!useContinuousIteration_.isSelected());

        panel.add(label);
        panel.add(useContinuousIteration_);
        panel.add(Box.createHorizontalGlue());
        panel.add(nextButton);

        return panel;
    }

    /**
     * The dropdown menu at the top for selecting a kernel type.
     * @return a dropdown/down component.
     */
    private JPanel createButtons() {
        JPanel buttonsPanel = new JPanel();
        resetButton = new JButton("Reset");
        resetButton.addActionListener(this);
        buttonsPanel.add(resetButton);
        return buttonsPanel;
    }

    public void reset() {
        generalSliderGroup_.reset();
    }

    /**
     * One of the sliders was moved.
     */
    public void sliderChanged(int sliderIndex, String sliderName, double value) {

        switch (sliderName) {
            case FLOOR_SLIDER:
                caveModel.setFloorThresh(value);
                break;
            case CEILING_SLIDER:
                caveModel.setCeilThresh(value);
                break;
            case LOSS_FACTOR_SLIDER:
                caveModel.setLossFactor(value);
                break;
            case EFFECT_FACTOR_SLIDER:
                caveModel.setEffectFactor(value);
                break;

            case BUMP_HEIGHT_SLIDER:
                caveModel.setBumpHeight(value);
                // specular highlight does not apply if no bumps
                bumpSliderGroup_.setEnabled(SPECULAR_PCT_SLIDER, value > 0);
                bumpSliderGroup_.setEnabled(LIGHT_SOURCE_ELEVATION_SLIDER, value > 0);
                break;
            case SPECULAR_PCT_SLIDER:
                caveModel.setSpecularPercent(value);
                break;
            case LIGHT_SOURCE_ELEVATION_SLIDER:
                caveModel.setLightSourceDescensionAngle(PI_D2 - value);
                break;
            case LIGHT_SOURCE_AZYMUTH_SLIDER:
                caveModel.setLightSourceAzymuthAngle(value);
                break;
            case SCALE_SLIDER:
                caveModel.setScale(value);
                simulator_.getInteractionHandler().setScale(value);
                break;

            case BRUSH_RADIUS_SLIDER:
                simulator_.getInteractionHandler().setBrushRadius((int) value);
                break;
            case BRUSH_STRENGTH_SLIDER:
                simulator_.getInteractionHandler().setBrushStrength(value);
                break;
            default: throw new IllegalArgumentException("Unexpected slider: " + sliderName);
        }
    }

    @Override
    public void itemStateChanged(ItemEvent e) {
        CaveProcessor.KernelType type = CaveProcessor.KernelType.valueOf(kernelChoice.getSelectedItem());
        caveModel.setKernelType(type);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource().equals(nextButton)) {
            caveModel.requestNextStep();
        }
        else if (e.getSource().equals(resetButton)) {
            caveModel.requestRestart();
        }
        else if (e.getSource().equals(useContinuousIteration_)) {
            boolean useCont = useContinuousIteration_.isSelected();
            caveModel.setDefaultUseContinuousIteration(useCont);
            nextButton.setEnabled(!useCont);
            if (!useCont) {
                // do one last step in case the rendering was interrupted.
                ThreadUtil.sleep(100);
                caveModel.requestNextStep();
            }
        }
        else throw new IllegalStateException("Unexpected button " + e.getSource());
    }
}
