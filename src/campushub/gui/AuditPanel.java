package campushub.gui;

import campushub.db.Database;
import campushub.ds.MyArrayList;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Audit Events page - the stack-style undo/audit log written every time
 * an operation (like a route query) runs. Uses Database.recentAuditEventsDetailed
 * so each field has its own table column instead of one squashed string.
 */
public class AuditPanel extends JPanel {

    private final AppContext ctx;
    private final JPanel tableHolder;

    public AuditPanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Audit Events");
        JLabel sub = explainerLabel(
                "A permanent, timestamped record of operations the system has performed - proof "
                + "the database is genuinely part of the running system, not just storage.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        tableHolder = new JPanel(new BorderLayout());
        tableHolder.setOpaque(false);
        add(tableHolder, BorderLayout.CENTER);

        JButton refresh = secondaryButton("Refresh");
        refresh.addActionListener(e -> load());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.setOpaque(false);
        south.add(refresh);
        add(south, BorderLayout.SOUTH);

        load();
    }

    private void load() {
        MyArrayList<Database.AuditEventRow> rows = ctx.db.recentAuditEventsDetailed(20);
        tableHolder.removeAll();

        if (rows.size() == 0) {
            JPanel empty = card();
            JLabel msg = new JLabel("No audit events yet - try Fastest Route to generate one.");
            msg.setFont(FONT_BODY);
            msg.setForeground(TEXT_MUTED);
            empty.add(msg);
            tableHolder.add(empty, BorderLayout.NORTH);
        } else {
            String[] cols = {"ID", "Event Type", "Category", "Details", "Timestamp"};
            Object[][] data = new Object[rows.size()][5];
            for (int i = 0; i < rows.size(); i++) {
                Database.AuditEventRow r = rows.get(i);
                data[i][0] = r.id;
                data[i][1] = r.eventType;
                data[i][2] = r.entityType;
                data[i][3] = r.entityId;
                data[i][4] = r.eventTime;
            }
            JTable table = table(cols, data);
            table.getColumnModel().getColumn(0).setMaxWidth(50);
            tableHolder.add(scroll(table), BorderLayout.CENTER);
        }
        tableHolder.revalidate();
        tableHolder.repaint();
    }
}
