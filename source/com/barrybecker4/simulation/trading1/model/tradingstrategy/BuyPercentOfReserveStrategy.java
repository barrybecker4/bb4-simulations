/** Copyright by Barry G. Becker, 2015. Licensed under MIT License: http://www.opensource.org/licenses/MIT  */
package com.barrybecker4.simulation.trading1.model.tradingstrategy;

import com.barrybecker4.simulation.trading1.options.ChangePolicy;
import com.barrybecker4.simulation.trading1.options.ui.ChangePolicyPanel;

import javax.swing.*;
import java.awt.*;

/**
 * When the market dips, we buy with a percentage of our reserve.
 * Conversely, when the market goes up by some threshold percent, we sell a percentage of what is invested.
 *
 * @author Barry Becker
 */
public class BuyPercentOfReserveStrategy extends AbstractTradingStrategy {

    private static final ChangePolicy DEFAULT_GAIN_POLICY = new ChangePolicy(0.02, 0.05);
    private static final ChangePolicy DEFAULT_LOSS_POLICY = new ChangePolicy(0.02, 0.05);

    private ChangePolicy gainPolicy = DEFAULT_GAIN_POLICY;
    private ChangePolicy lossPolicy = DEFAULT_LOSS_POLICY;

    private ChangePolicyPanel gainPolicyPanel;
    private ChangePolicyPanel lossPolicyPanel;


    public String getName() {
        return "percent of reserve";
    }

    public String getDescription() {
        return "When the marked goes up, we sell a percent of investment; when it goes down we buy a percent of reserve";
    }

    /**
     * if this new price triggers a transaction, then do it
     */
    @Override
    public MarketPosition updateInvestment(double stockPrice) {

        double pctChange = (stockPrice - priceAtLastTransaction) / priceAtLastTransaction;
        if (pctChange >= gainPolicy.getChangePercent()) {
            // sell, and take some profit. Assume we can sell partial shares
            double sharesToSell = gainPolicy.getTransactPercent() * sharesOwned;
            sell(sharesToSell, stockPrice);
        }
        else if (-pctChange >= lossPolicy.getChangePercent()) {
            // buy more because its cheaper
            double amountToInvest = lossPolicy.getTransactPercent() * reserve;
            buy(amountToInvest, stockPrice);
        }
        return new MarketPosition(invested, reserve, sharesOwned);
    }


    /** The UI to allow the user to configure the options */
    public JPanel getOptionsUI() {
        JPanel strategyPanel = new JPanel(new BorderLayout());
        strategyPanel.setBorder(BorderFactory.createEtchedBorder());

        gainPolicyPanel = new ChangePolicyPanel("% gain which triggers next transaction",
                "% of current investment to sell on gain",
                gainPolicy);
        lossPolicyPanel = new ChangePolicyPanel("% market loss which triggers next transaction",
                "% of current reserve to use to buy on loss",
                lossPolicy);

        gainPolicyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);
        lossPolicyPanel.setAlignmentX(Component.CENTER_ALIGNMENT);

        strategyPanel.add(gainPolicyPanel, BorderLayout.NORTH);
        strategyPanel.add(lossPolicyPanel, BorderLayout.CENTER);

        return strategyPanel;
    }

    /** Call when OK button is pressed to persist selections */
    public void acceptSelectedOptions() {
        this.gainPolicy = gainPolicyPanel.getChangePolicy();
        this.lossPolicy = lossPolicyPanel.getChangePolicy();
    }
}
