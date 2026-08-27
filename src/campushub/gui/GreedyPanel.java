package campushub.gui;

import campushub.algo.BudgetSelector;
import campushub.algo.GreedyAssigner;
import campushub.ds.MyArrayList;
import campushub.model.ServiceRequest;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Greedy vs Optimal page - runs the project's built-in counterexample
 * (GreedyAssigner.greedyFailureExample) through both the greedy
 * ratio-based selector and the optimal DP selector, side by side, so the
 * gap between "fast but sometimes wrong" and "always optimal" is visible.
 */
public class GreedyPanel extends JPanel {

    public GreedyPanel(AppContext ctx) {
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Greedy vs Optimal");
        JLabel sub = explainerLabel(
                "Greedy algorithms make the best immediate choice, while the optimal solution "
                + "considers the complete problem space. This is the project's required "
                + "counterexample: a case built specifically to show greedy getting it wrong.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        MyArrayList<ServiceRequest> instance = GreedyAssigner.greedyFailureExample();
        int budget = 50;
        GreedyAssigner.Result greedy = GreedyAssigner.selectByRatio(instance, budget);
        BudgetSelector.Result dp = BudgetSelector.selectWithinBudget(instance, budget);
        boolean greedyLoses = greedy.totalBenefit < dp.totalBenefit;

        JPanel candidates = card();
        candidates.setLayout(new BorderLayout(0, 8));
        candidates.setAlignmentX(Component.LEFT_ALIGNMENT);
        candidates.setMaximumSize(new Dimension(Integer.MAX_VALUE, 160));
        JLabel candTitle = new JLabel("Candidate tickets (budget = GHS " + budget + ")");
        candTitle.setFont(FONT_BUTTON);
        candTitle.setForeground(TEXT_DARK);
        String[] cols = {"Ticket", "Cost (GHS)", "Benefit", "Ratio"};
        Object[][] rows = new Object[instance.size()][4];
        for (int i = 0; i < instance.size(); i++) {
            ServiceRequest r = instance.get(i);
            rows[i][0] = r.getId();
            rows[i][1] = String.format("%.0f", r.getCost());
            rows[i][2] = r.getBenefit();
            rows[i][3] = String.format("%.2f", r.getBenefit() / r.getCost());
        }
        candidates.add(candTitle, BorderLayout.NORTH);
        candidates.add(scroll(table(cols, rows)), BorderLayout.CENTER);

        JPanel compare = new JPanel(new GridLayout(1, 2, 16, 0));
        compare.setOpaque(false);
        compare.setAlignmentX(Component.LEFT_ALIGNMENT);
        compare.setMaximumSize(new Dimension(Integer.MAX_VALUE, 140));
        compare.add(resultCard("Greedy (best ratio first)", greedy.totalBenefit, greedy.totalCost,
                idsOf(greedy.funded), greedyLoses ? AMBER : GREEN));
        compare.add(resultCard("Optimal (dynamic programming)", dp.totalBenefit, dp.totalCost,
                idsOf(dp.fundedRequests), GREEN));

        JPanel conclusion = card();
        conclusion.setAlignmentX(Component.LEFT_ALIGNMENT);
        conclusion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        conclusion.setLayout(new BorderLayout());
        JLabel concLbl = new JLabel(greedyLoses
                ? "\u26a0 Greedy is WORSE here (" + greedy.totalBenefit + " < " + dp.totalBenefit
                  + "): taking the best ratio first blocks a better combination later."
                : "Greedy happened to match optimal on this instance.");
        concLbl.setFont(FONT_BUTTON);
        concLbl.setForeground(greedyLoses ? RED : GREEN);
        conclusion.add(concLbl, BorderLayout.CENTER);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(candidates);
        body.add(Box.createVerticalStrut(16));
        body.add(compare);
        body.add(Box.createVerticalStrut(16));
        body.add(conclusion);
        add(body, BorderLayout.CENTER);
    }

    private JPanel resultCard(String title, int benefit, double cost, String ids, Color accent) {
        JPanel p = card();
        p.setLayout(new BorderLayout(0, 8));
        JLabel t = new JLabel(title);
        t.setFont(FONT_BUTTON);
        t.setForeground(TEXT_DARK);
        JLabel stats = new JLabel("Benefit " + benefit + "  \u00b7  Cost GHS " + String.format("%.0f", cost));
        stats.setFont(FONT_SUBTLE);
        stats.setForeground(TEXT_MUTED);
        JLabel idsLbl = pill(ids, accent);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(t);
        top.add(Box.createVerticalStrut(4));
        top.add(stats);

        p.add(top, BorderLayout.NORTH);
        JPanel idsWrap = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        idsWrap.setOpaque(false);
        idsWrap.add(idsLbl);
        p.add(idsWrap, BorderLayout.SOUTH);
        return p;
    }

    private String idsOf(MyArrayList<ServiceRequest> list) {
        StringBuilder sb = new StringBuilder("{");
        for (int i = 0; i < list.size(); i++) {
            if (i > 0) sb.append(", ");
            sb.append(list.get(i).getId());
        }
        return sb.append("}").toString();
    }
}
