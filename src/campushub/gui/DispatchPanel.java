package campushub.gui;

import campushub.algo.PriorityDispatcher;
import campushub.ds.MyArrayList;
import campushub.model.ServiceRequest;

import javax.swing.*;
import java.awt.*;

import static campushub.gui.UIComponents.*;

/**
 * Priority Service Dispatch page. Loads every request from the database
 * into the project's own MyMinHeap-backed PriorityDispatcher and shows
 * the next N tickets in true dispatch order (most urgent first, FIFO
 * tie-break) - the exact same class the console menu calls.
 */
public class DispatchPanel extends JPanel {

    private final AppContext ctx;
    private final JPanel tableHolder;

    public DispatchPanel(AppContext ctx) {
        this.ctx = ctx;
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Priority Service Dispatch");
        JLabel sub = explainerLabel(
                "Requests are ranked by urgency using the project's custom priority dispatch "
                + "implementation - a hand-built min-heap (MyMinHeap), the same one used by "
                + "PriorityDispatcher across the whole system.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        tableHolder = new JPanel(new BorderLayout());
        tableHolder.setOpaque(false);
        add(tableHolder, BorderLayout.CENTER);

        JButton run = primaryButton("Dispatch Next Requests");
        run.addActionListener(e -> runDispatch());
        JPanel south = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        south.setOpaque(false);
        south.add(run);
        add(south, BorderLayout.SOUTH);

        runDispatch();
    }

    private void runDispatch() {
        MyArrayList<ServiceRequest> reqs = ctx.db.loadRequests();
        PriorityDispatcher dispatcher = new PriorityDispatcher();
        for (int i = 0; i < reqs.size(); i++) dispatcher.addRequest(reqs.get(i));

        int show = Math.min(15, dispatcher.waitingCount());
        Object[][] rows = new Object[show][5];
        for (int i = 0; i < show; i++) {
            ServiceRequest r = dispatcher.dispatchNext();
            rows[i][0] = i + 1;
            rows[i][1] = r.getId();
            rows[i][2] = r.getIssueType();
            rows[i][3] = ctx.nameWithId(r.getLocationId());
            rows[i][4] = "L" + r.getUrgencyLevel();
        }

        String[] cols = {"Rank", "Ticket ID", "Service Type", "Location", "Urgency"};
        JTable table = table(cols, rows);
        table.getColumnModel().getColumn(0).setMaxWidth(60);
        table.getColumnModel().getColumn(4).setMaxWidth(80);
        table.getColumnModel().getColumn(4).setCellRenderer(new UrgencyCellRenderer());

        tableHolder.removeAll();
        tableHolder.add(scroll(table), BorderLayout.CENTER);

        JLabel footer = new JLabel(dispatcher.waitingCount() + " more tickets waiting in the queue.");
        footer.setFont(FONT_SMALL);
        footer.setForeground(TEXT_MUTED);
        footer.setBorder(pad(8, 4, 0, 0));
        tableHolder.add(footer, BorderLayout.SOUTH);

        tableHolder.revalidate();
        tableHolder.repaint();
    }

    /** Renders the urgency column as a colored pill instead of plain text. */
    private static class UrgencyCellRenderer extends javax.swing.table.DefaultTableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable t, Object value, boolean sel, boolean focus, int row, int col) {
            JLabel base = (JLabel) super.getTableCellRendererComponent(t, value, sel, focus, row, col);
            String s = String.valueOf(value);
            int level = 3;
            try { level = Integer.parseInt(s.substring(1)); } catch (Exception ignored) {}
            JLabel pill = pill(s, urgencyColor(level));
            pill.setHorizontalAlignment(SwingConstants.CENTER);
            return pill;
        }
    }
}
