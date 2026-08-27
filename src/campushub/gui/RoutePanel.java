package campushub.gui;

import campushub.algo.ShortestRoute;
import campushub.ds.MyArrayList;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Fastest Route page - Dijkstra's algorithm (campushub.algo.ShortestRoute)
 * over the real campus road graph. Start/destination are chosen from
 * dropdowns populated from the actual database instead of typed IDs.
 */
public class RoutePanel extends JPanel {

    private final AppContext ctx;
    private JComboBox<LocationItem> startBox;
    private JComboBox<LocationItem> destBox;
    private JPanel resultHolder;

    public RoutePanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Fastest Route");
        JLabel sub = explainerLabel(
                "Finds the shortest weighted path between two campus locations using "
                + "Dijkstra's algorithm (ShortestRoute.findShortestPath), accounting for road "
                + "condition and distance, not just hop count.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel controls = card();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        MyArrayList<String> locs = ctx.graph.allLocations();
        LocationItem[] items = new LocationItem[locs.size()];
        for (int i = 0; i < locs.size(); i++) items[i] = new LocationItem(locs.get(i), ctx.nameOf(locs.get(i)));
        java.util.Arrays.sort(items, (a, b) -> a.name.compareToIgnoreCase(b.name));

        startBox = new JComboBox<>(items);
        destBox = new JComboBox<>(items);
        if (items.length > 1) destBox.setSelectedIndex(1);

        controls.add(labeled("Start location", startBox));
        controls.add(labeled("Destination", destBox));
        JButton calc = primaryButton("Calculate Route");
        calc.addActionListener(e -> calculate());
        controls.add(calc);

        resultHolder = new JPanel();
        resultHolder.setOpaque(false);
        resultHolder.setLayout(new BoxLayout(resultHolder, BoxLayout.Y_AXIS));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(controls, BorderLayout.NORTH);
        wrapper.add(resultHolder, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(label);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_MUTED);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setPreferredSize(new Dimension(220, 30));
        p.add(l);
        p.add(field);
        return p;
    }

    private void calculate() {
        LocationItem from = (LocationItem) startBox.getSelectedItem();
        LocationItem to = (LocationItem) destBox.getSelectedItem();
        resultHolder.removeAll();

        if (from == null || to == null) return;
        ShortestRoute.Result res = ShortestRoute.findShortestPath(ctx.graph, from.id, to.id);

        JPanel resultCard = card();
        resultCard.setLayout(new BorderLayout(0, 10));
        resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        resultCard.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1), pad(16, 18, 16, 18)));

        if (!res.isReachable()) {
            JLabel none = new JLabel("No route found between " + from.name + " and " + to.name + ".");
            none.setFont(FONT_BODY);
            none.setForeground(RED);
            resultCard.add(none, BorderLayout.CENTER);
        } else {
            JPanel pathRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 6, 4));
            pathRow.setOpaque(false);
            for (int i = 0; i < res.path.size(); i++) {
                JLabel node = pill(ctx.nameOf(res.path.get(i)), i == 0 ? GREEN : (i == res.path.size() - 1 ? RED : ACCENT_BLUE));
                pathRow.add(node);
                if (i < res.path.size() - 1) {
                    JLabel arrow = new JLabel("\u2192");
                    arrow.setForeground(TEXT_MUTED);
                    pathRow.add(arrow);
                }
            }

            JLabel distLabel = new JLabel(String.format(
                    "Total weighted distance: %.2f  \u00b7  %d stops",
                    res.totalDistance, res.path.size()));
            distLabel.setFont(FONT_BUTTON);
            distLabel.setForeground(TEXT_DARK);

            JLabel meta = new JLabel(from.name + "  \u2192  " + to.name);
            meta.setFont(FONT_SUBTLE);
            meta.setForeground(TEXT_MUTED);

            JPanel top = new JPanel();
            top.setOpaque(false);
            top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
            meta.setAlignmentX(Component.LEFT_ALIGNMENT);
            distLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
            top.add(meta);
            top.add(Box.createVerticalStrut(6));
            top.add(distLabel);

            resultCard.add(top, BorderLayout.NORTH);
            resultCard.add(pathRow, BorderLayout.CENTER);

            // this matches exactly what the console menu logs on a route query
            ctx.db.recordAlgorithmRun("Dijkstra", ctx.graph.locationCount(), 0L, 0L);
            ctx.db.pushAuditEvent("ROUTE_QUERY", "route", from.id + "->" + to.id,
                    String.format("distance=%.2f", res.totalDistance));

            JLabel logged = new JLabel("Logged to algorithm_runs and audit_events.");
            logged.setFont(FONT_SMALL);
            logged.setForeground(GREEN);
            logged.setBorder(pad(8, 0, 0, 0));
            resultCard.add(logged, BorderLayout.SOUTH);
        }

        resultHolder.add(Box.createVerticalStrut(14));
        resultHolder.add(resultCard);
        resultHolder.revalidate();
        resultHolder.repaint();
    }

    private static class LocationItem {
        final String id, name;
        LocationItem(String id, String name) { this.id = id; this.name = name; }
        @Override public String toString() { return name + "  (" + id + ")"; }
    }
}
