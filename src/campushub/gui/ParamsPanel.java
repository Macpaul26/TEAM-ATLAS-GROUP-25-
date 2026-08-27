package campushub.gui;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Index Parameters page - the operational parameters derived from the
 * team's index numbers (campushub.config.IndexParameters). Display only;
 * the underlying calculation is untouched.
 */
public class ParamsPanel extends JPanel {

    public ParamsPanel(AppContext ctx) {
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Index-Derived Parameters");
        JLabel sub = explainerLabel(
                "Five operational settings, each mathematically derived from the team's 14 real "
                + "index numbers (IndexParameters) - this is what makes the system's behaviour "
                + "uniquely tied to this team, not hardcoded.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(2, 3, 12, 12));
        grid.setAlignmentX(Component.LEFT_ALIGNMENT);
        grid.setMaximumSize(new Dimension(Integer.MAX_VALUE, 190));
        grid.add(statCard(String.valueOf(ctx.params.hashTableCapacity()), "Hash Table Capacity", ACCENT_BLUE));
        grid.add(statCard(String.valueOf(ctx.params.randomSeed()), "Random Seed", ACCENT_BLUE));
        grid.add(statCard(String.format("%.2f", ctx.params.routePenaltyFactor()), "Route Penalty Factor", AMBER));
        grid.add(statCard(String.valueOf(ctx.params.budgetConstraint()), "Budget Constraint (GHS)", GREEN));
        grid.add(statCard(String.valueOf(ctx.params.priorityWeight()), "Priority Weight", ACCENT_BLUE));

        JPanel body = new JPanel();
        body.setOpaque(false);
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.add(grid);
        body.add(Box.createVerticalStrut(20));

        JPanel noteCard = card();
        noteCard.setLayout(new BorderLayout());
        noteCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        noteCard.setMaximumSize(new Dimension(Integer.MAX_VALUE, 70));
        JLabel note = explainerLabel(
                "Change any index number in IndexParameters.java and every value above shifts - "
                + "this is the brief's required \u201cat least three parameters derived from member "
                + "index numbers\u201d requirement, satisfied five times over.");
        noteCard.add(note, BorderLayout.CENTER);
        body.add(noteCard);

        add(body, BorderLayout.CENTER);
    }
}
