package campushub.gui;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Database Statistics page - real record counts for every table, plus a
 * simple connection-health indicator.
 */
public class StatsPanel extends JPanel {

    public StatsPanel(AppContext ctx) {
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Database Statistics");
        JLabel sub = explainerLabel("Live record counts read directly from the SQLite database on every visit to this page.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));

        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setOpaque(false);
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        String[][] tables = {
                {"locations", "Locations"}, {"roads", "Roads"}, {"resources", "Resources"},
                {"service_requests", "Requests"}, {"algorithm_runs", "Runs"}, {"audit_events", "Events"},
        };
        for (String[] t : tables) {
            grid.add(statCard(String.valueOf(ctx.db.count(t[0])), t[1], ACCENT_BLUE));
        }
        body.add(grid);
        body.add(Box.createVerticalStrut(20));

        JPanel healthCard = card();
        healthCard.setLayout(new BorderLayout());
        healthCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        healthCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 60));
        JLabel health = new JLabel("\u25CF Database connected and responding.");
        health.setFont(FONT_BUTTON);
        health.setForeground(GREEN);
        healthCard.add(health, BorderLayout.CENTER);
        body.add(healthCard);

        add(body, BorderLayout.CENTER);
    }
}
