package campushub.gui;

import campushub.algo.GraphTraversal;
import campushub.ds.MyArrayList;
import campushub.ds.MySet;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Reachable Locations page - runs BFS and DFS (campushub.algo.GraphTraversal)
 * from a chosen starting location over the real campus graph.
 */
public class ReachabilityPanel extends JPanel {

    private final AppContext ctx;
    private JComboBox<LocationItem> startBox;
    private JPanel resultHolder;

    public ReachabilityPanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Reachable Locations");
        JLabel sub = explainerLabel(
                "<b>BFS</b> explores locations level by level, one \u201cring\u201d of distance at a "
                + "time. <b>DFS</b> explores as far as possible along one path before "
                + "backtracking. Both are custom implementations over the real campus graph.");
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
        startBox.setPreferredSize(new Dimension(240, 30));

        JLabel lbl = new JLabel("Start location");
        lbl.setFont(FONT_SMALL);
        lbl.setForeground(TEXT_MUTED);
        JPanel boxWrap = new JPanel();
        boxWrap.setOpaque(false);
        boxWrap.setLayout(new BoxLayout(boxWrap, BoxLayout.Y_AXIS));
        lbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        startBox.setAlignmentX(Component.LEFT_ALIGNMENT);
        boxWrap.add(lbl);
        boxWrap.add(startBox);
        controls.add(boxWrap);

        JButton bfsBtn = primaryButton("Run BFS");
        JButton dfsBtn = secondaryButton("Run DFS");
        bfsBtn.addActionListener(e -> run(true));
        dfsBtn.addActionListener(e -> run(false));
        controls.add(bfsBtn);
        controls.add(dfsBtn);

        resultHolder = new JPanel();
        resultHolder.setOpaque(false);
        resultHolder.setLayout(new BoxLayout(resultHolder, BoxLayout.Y_AXIS));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(controls, BorderLayout.NORTH);
        wrapper.add(resultHolder, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private void run(boolean bfs) {
        LocationItem start = (LocationItem) startBox.getSelectedItem();
        if (start == null) return;

        MyArrayList<String> order = bfs ? GraphTraversal.bfs(ctx.graph, start.id) : GraphTraversal.dfs(ctx.graph, start.id);
        MySet<String> reach = GraphTraversal.reachableFrom(ctx.graph, start.id);

        JPanel resultCard = card();
        resultCard.setLayout(new BorderLayout(0, 10));
        resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        JLabel methodLbl = new JLabel((bfs ? "BFS" : "DFS") + " from " + start.name);
        methodLbl.setFont(FONT_BUTTON);
        methodLbl.setForeground(TEXT_DARK);

        JLabel countLbl = new JLabel(reach.size() + " of " + ctx.graph.locationCount() + " locations reachable");
        countLbl.setFont(FONT_SUBTLE);
        countLbl.setForeground(TEXT_MUTED);

        JPanel top = new JPanel();
        top.setOpaque(false);
        top.setLayout(new BoxLayout(top, BoxLayout.Y_AXIS));
        methodLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        countLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        top.add(methodLbl);
        top.add(Box.createVerticalStrut(4));
        top.add(countLbl);

        WrapPanel sequence = new WrapPanel(6, 4);
        sequence.setOpaque(false);
        int show = Math.min(order.size(), 20);
        for (int i = 0; i < show; i++) {
            sequence.add(pill(ctx.nameOf(order.get(i)), i == 0 ? GREEN : ACCENT_BLUE));
        }
        if (order.size() > show) {
            JLabel more = new JLabel("+ " + (order.size() - show) + " more");
            more.setFont(FONT_SMALL);
            more.setForeground(TEXT_MUTED);
            sequence.add(more);
        }

        resultCard.add(top, BorderLayout.NORTH);
        resultCard.add(scroll(sequence), BorderLayout.CENTER);
        resultCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 400));

        resultHolder.removeAll();
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
