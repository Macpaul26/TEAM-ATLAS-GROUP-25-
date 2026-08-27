package campushub.gui;

import campushub.algo.MinimumSpanningTree;
import campushub.ds.MyArrayList;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Network Optimizer page - runs both Prim's and Kruskal's algorithms
 * (campushub.algo.MinimumSpanningTree) over the real campus graph and
 * shows that two completely different strategies converge on the same
 * total cost.
 */
public class NetworkPanel extends JPanel {

    public NetworkPanel(AppContext ctx) {
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Minimum Connection Network");
        JLabel sub = explainerLabel(
                "The cheapest possible set of roads that still connects every campus location, "
                + "computed two different ways: Prim's algorithm (grows one tree outward) and "
                + "Kruskal's algorithm (sorts every road, adds the cheapest that doesn't form a "
                + "cycle, using a disjoint-set/union-find structure).");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        MyArrayList<String> locs = ctx.graph.allLocations();
        MinimumSpanningTree.Result prim = MinimumSpanningTree.prim(ctx.graph, locs.get(0));
        MinimumSpanningTree.Result kruskal = MinimumSpanningTree.kruskal(ctx.graph);
        boolean agree = Math.abs(prim.totalWeight - kruskal.totalWeight) < 0.01;

        JPanel compare = new JPanel(new GridLayout(1, 2, 16, 0));
        compare.setOpaque(false);
        compare.add(algorithmCard("Prim's Algorithm", prim.edges.size(), prim.totalWeight, ctx));
        compare.add(algorithmCard("Kruskal's Algorithm", kruskal.edges.size(), kruskal.totalWeight, ctx));

        JPanel conclusion = card();
        conclusion.setLayout(new BorderLayout());
        JLabel concLbl = new JLabel(agree
                ? "\u2713 Both algorithms agree on the optimal total cost: " + String.format("%.2f", prim.totalWeight)
                : "\u26a0 Totals differ - this should not happen for a connected graph.");
        concLbl.setFont(FONT_BUTTON);
        concLbl.setForeground(agree ? GREEN : RED);
        conclusion.add(concLbl, BorderLayout.CENTER);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        compare.setAlignmentX(Component.LEFT_ALIGNMENT);
        compare.setMaximumSize(new Dimension(Integer.MAX_VALUE, 340));
        conclusion.setAlignmentX(Component.LEFT_ALIGNMENT);
        conclusion.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        body.add(compare);
        body.add(Box.createVerticalStrut(16));
        body.add(conclusion);

        add(body, BorderLayout.CENTER);
    }

    private JPanel algorithmCard(String name, int edgeCount, double totalWeight, AppContext ctx) {
        JPanel p = card();
        p.setLayout(new BorderLayout(0, 10));

        JLabel title = new JLabel(name);
        title.setFont(FONT_BUTTON);
        title.setForeground(TEXT_DARK);

        JLabel stats = new JLabel(edgeCount + " edges  \u00b7  total weight " + String.format("%.2f", totalWeight));
        stats.setFont(FONT_SUBTLE);
        stats.setForeground(TEXT_MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(title);
        top.add(Box.createVerticalStrut(2));
        top.add(stats);

        p.add(top, BorderLayout.NORTH);
        return p;
    }
}
