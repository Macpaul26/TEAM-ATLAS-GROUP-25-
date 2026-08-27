package campushub.gui;

import campushub.algo.BudgetSelector;
import campushub.ds.MyArrayList;
import campushub.model.ServiceRequest;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Budget Optimizer page - the 0/1 knapsack dynamic-programming algorithm
 * (campushub.algo.BudgetSelector) choosing the best combination of real
 * service requests that fits within a given GHS budget.
 */
public class BudgetPanel extends JPanel {

    private final AppContext ctx;
    private JTextField budgetField;
    private JPanel resultHolder;

    public BudgetPanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Budget Optimizer");
        JLabel sub = explainerLabel(
                "The optimizer selects the best combination of requests that can be handled "
                + "within the available budget, using 0/1 knapsack dynamic programming "
                + "(BudgetSelector.selectWithinBudget) - not just the cheapest or highest-value "
                + "tickets in isolation, but the provably best combination.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel controls = card();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));
        budgetField = new JTextField(String.valueOf(ctx.params.budgetConstraint()), 8);
        budgetField.setPreferredSize(new Dimension(100, 30));
        JLabel lbl = new JLabel("Budget (GHS)");
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        JPanel fieldWrap = new JPanel();
        fieldWrap.setOpaque(false);
        fieldWrap.setLayout(new BoxLayout(fieldWrap, BoxLayout.Y_AXIS));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        budgetField.setAlignmentX(Component.LEFT_ALIGNMENT);
        fieldWrap.add(lbl);
        fieldWrap.add(budgetField);
        controls.add(fieldWrap);

        JButton run = primaryButton("Optimize");
        run.addActionListener(e -> optimize());
        controls.add(run);

        resultHolder = new JPanel();
        resultHolder.setOpaque(false);
        resultHolder.setLayout(new BoxLayout(resultHolder, BoxLayout.Y_AXIS));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(controls, BorderLayout.NORTH);
        wrapper.add(resultHolder, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        optimize();
    }

    private void optimize() {
        int budget;
        try { budget = Integer.parseInt(budgetField.getText().trim()); }
        catch (NumberFormatException e) { budget = ctx.params.budgetConstraint(); }

        MyArrayList<ServiceRequest> reqs = ctx.db.loadRequests();
        MyArrayList<ServiceRequest> subset = new MyArrayList<>();
        for (int i = 0; i < Math.min(40, reqs.size()); i++) subset.add(reqs.get(i));

        BudgetSelector.Result res = BudgetSelector.selectWithinBudget(subset, budget);

        JPanel summary = new JPanel(new GridLayout(1, 3, 12, 0));
        summary.setOpaque(false);
        summary.setAlignmentX(Component.LEFT_ALIGNMENT);
        summary.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        summary.add(statCard(String.valueOf(budget), "Available Budget (GHS)", ACCENT_BLUE));
        summary.add(statCard(String.valueOf(res.fundedRequests.size()), "Requests Selected", GREEN));
        summary.add(statCard(String.format("%.0f", res.totalCost), "Total Cost (GHS)", AMBER));

        String[] cols = {"Ticket ID", "Location", "Cost (GHS)", "Benefit"};
        Object[][] rows = new Object[res.fundedRequests.size()][4];
        for (int i = 0; i < res.fundedRequests.size(); i++) {
            ServiceRequest r = res.fundedRequests.get(i);
            rows[i][0] = r.getId();
            rows[i][1] = ctx.nameWithId(r.getLocationId());
            rows[i][2] = String.format("%.0f", r.getCost());
            rows[i][3] = r.getBenefit();
        }
        JTable table = table(cols, rows);

        resultHolder.removeAll();
        resultHolder.add(Box.createVerticalStrut(14));
        resultHolder.add(summary);
        resultHolder.add(Box.createVerticalStrut(14));
        resultHolder.add(scroll(table));
        resultHolder.revalidate();
        resultHolder.repaint();
    }
}
