package campushub.gui;

import javax.swing.*;
import java.awt.*;
import java.util.function.Consumer;

import static campushub.gui.UIComponents.*;

/**
 * The home page: real database counts as stat cards, a handful of quick
 * actions that jump straight to the corresponding page, and a short
 * plain-language description of what the system does.
 */
public class DashboardPanel extends JPanel {

    public DashboardPanel(AppContext ctx, Consumer<String> navigate) {
        setLayout(new BorderLayout(0, 16));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel(new BorderLayout());
        head.setOpaque(false);
        JLabel title = sectionLabel("Dashboard");
        JLabel sub = explainerLabel("Live overview of the campus service hub, pulled straight from the database.");
        JPanel headText = new JPanel();
        headText.setOpaque(false);
        headText.setLayout(new BoxLayout(headText, BoxLayout.Y_AXIS));
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        headText.add(title);
        headText.add(sub);
        head.add(headText, BorderLayout.WEST);
        add(head, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        // ---- stat cards ----
        JPanel stats = new JPanel(new GridLayout(1, 6, 12, 0));
        stats.setOpaque(false);
        stats.setAlignmentX(Component.LEFT_ALIGNMENT);
        stats.setMaximumSize(new Dimension(Integer.MAX_VALUE, 90));
        stats.add(statCard(String.valueOf(ctx.db.count("locations")), "Locations", ACCENT_BLUE));
        stats.add(statCard(String.valueOf(ctx.db.count("roads")), "Roads", ACCENT_BLUE));
        stats.add(statCard(String.valueOf(ctx.db.count("service_requests")), "Requests", AMBER));
        stats.add(statCard(String.valueOf(ctx.db.count("resources")), "Resources", GREEN));
        stats.add(statCard(String.valueOf(ctx.db.count("algorithm_runs")), "Runs", ACCENT_BLUE));
        stats.add(statCard(String.valueOf(ctx.db.count("audit_events")), "Events", TEXT_MUTED));
        body.add(stats);
        body.add(Box.createVerticalStrut(20));

        // ---- quick actions ----
        JLabel qaLabel = sectionLabel("Quick Actions");
        qaLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(qaLabel);
        body.add(Box.createVerticalStrut(10));

        JPanel actions = new JPanel(new GridLayout(1, 4, 12, 0));
        actions.setOpaque(false);
        actions.setAlignmentX(Component.LEFT_ALIGNMENT);
        actions.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        actions.add(quickActionCard("Dispatch Next Ticket", "Priority queue", () -> navigate.accept("dispatch")));
        actions.add(quickActionCard("Find Fastest Route", "Dijkstra", () -> navigate.accept("route")));
        actions.add(quickActionCard("Check Reachability", "BFS / DFS", () -> navigate.accept("reach")));
        actions.add(quickActionCard("Optimize Budget", "0/1 Knapsack", () -> navigate.accept("budget")));
        body.add(actions);
        body.add(Box.createVerticalStrut(24));

        // ---- system overview ----
        JLabel ovLabel = sectionLabel("System Overview");
        ovLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
        body.add(ovLabel);
        body.add(Box.createVerticalStrut(10));

        JPanel overviewCard = card();
        overviewCard.setLayout(new BorderLayout());
        overviewCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        overviewCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
        JLabel overviewText = explainerLabel(
                "Ghana Campus Service Hub is a smart operations platform that uses custom data "
                + "structures and algorithms to prioritize service requests, calculate optimal "
                + "routes, analyze network connectivity, and optimize operational decisions "
                + "across the University of Ghana campus. Every figure on this screen is read "
                + "live from the project's SQLite database - nothing here is hardcoded.");
        overviewCard.add(overviewText, BorderLayout.CENTER);
        body.add(overviewCard);

        add(body, BorderLayout.CENTER);
    }

    private JPanel quickActionCard(String title, String subtitle, Runnable onClick) {
        JPanel p = card();
        p.setLayout(new BorderLayout(0, 2));
        p.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel t = new JLabel(title);
        t.setFont(FONT_BUTTON);
        t.setForeground(TEXT_DARK);
        JLabel s = new JLabel(subtitle);
        s.setFont(FONT_SMALL);
        s.setForeground(TEXT_MUTED);

        p.add(t, BorderLayout.NORTH);
        p.add(s, BorderLayout.SOUTH);

        p.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { onClick.run(); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) { p.setBackground(new Color(0xF7, 0xFA, 0xFF)); }
            @Override public void mouseExited(java.awt.event.MouseEvent e) { p.setBackground(CARD_BG); }
        });
        return p;
    }
}
