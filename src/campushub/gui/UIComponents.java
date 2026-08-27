package campushub.gui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

/**
 * Shared visual language for the whole GUI: colors, fonts, and small
 * reusable builders (stat cards, section headers, tables, buttons, pills)
 * so every page looks like part of the same application instead of
 * being styled ad hoc.
 *
 * Palette: deep navy/charcoal for structure (header, sidebar), a soft
 * off-white for content areas, a restrained blue accent for primary
 * actions, green for healthy/successful states, amber/red reserved for
 * warnings and urgency - not used decoratively elsewhere.
 */
public final class UIComponents {

    private UIComponents() {}

    /**
     * A FlowLayout panel that wraps onto new lines inside a JScrollPane
     * instead of extending infinitely sideways (Swing's default FlowLayout
     * + JScrollPane combination scrolls horizontally rather than wrapping,
     * since the viewport otherwise gives the panel unlimited width).
     */
    public static class WrapPanel extends JPanel implements Scrollable {
        public WrapPanel(int hgap, int vgap) {
            super(new FlowLayout(FlowLayout.LEFT, hgap, vgap));
        }
        @Override public Dimension getPreferredScrollableViewportSize() { return getPreferredSize(); }
        @Override public int getScrollableUnitIncrement(Rectangle visibleRect, int orientation, int direction) { return 16; }
        @Override public int getScrollableBlockIncrement(Rectangle visibleRect, int orientation, int direction) { return 64; }
        @Override public boolean getScrollableTracksViewportWidth() { return true; }
        @Override public boolean getScrollableTracksViewportHeight() { return false; }

        /**
         * FlowLayout's own getPreferredSize() only reports the width/height
         * of ONE unwrapped row, which makes a JScrollPane think there's
         * almost nothing to scroll even when the layout visually wraps
         * onto many rows underneath. This recomputes the real wrapped
         * height based on the panel's actual current width.
         */
        @Override
        public Dimension getPreferredSize() {
            int targetWidth = getWidth();
            if (targetWidth <= 0 && getParent() != null) targetWidth = getParent().getWidth();
            if (targetWidth <= 0) return super.getPreferredSize();

            FlowLayout fl = (FlowLayout) getLayout();
            Insets insets = getInsets();
            int maxRowWidth = targetWidth - insets.left - insets.right;
            int hgap = fl.getHgap(), vgap = fl.getVgap();

            int rowWidth = 0, rowHeight = 0, totalHeight = vgap;
            for (Component c : getComponents()) {
                if (!c.isVisible()) continue;
                Dimension d = c.getPreferredSize();
                if (rowWidth > 0 && rowWidth + hgap + d.width > maxRowWidth) {
                    totalHeight += rowHeight + vgap;
                    rowWidth = 0;
                    rowHeight = 0;
                }
                rowWidth += (rowWidth > 0 ? hgap : 0) + d.width;
                rowHeight = Math.max(rowHeight, d.height);
            }
            totalHeight += rowHeight + vgap;
            return new Dimension(maxRowWidth, totalHeight);
        }
    }

    // ---- palette ----
    public static final Color NAVY        = new Color(0x0F, 0x22, 0x3B);
    public static final Color NAVY_LIGHT  = new Color(0x1B, 0x35, 0x57);
    public static final Color CONTENT_BG  = new Color(0xF4, 0xF6, 0xF9);
    public static final Color CARD_BG     = Color.WHITE;
    public static final Color BORDER      = new Color(0xE2, 0xE6, 0xEC);
    public static final Color TEXT_DARK   = new Color(0x1E, 0x29, 0x3B);
    public static final Color TEXT_MUTED  = new Color(0x64, 0x74, 0x8B);
    public static final Color TEXT_ON_NAVY       = new Color(0xE7, 0xEC, 0xF3);
    public static final Color TEXT_ON_NAVY_MUTED = new Color(0x93, 0xA5, 0xBF);
    public static final Color ACCENT_BLUE = new Color(0x2F, 0x6F, 0xED);
    public static final Color GREEN       = new Color(0x1E, 0xA1, 0x5E);
    public static final Color AMBER       = new Color(0xC8, 0x7C, 0x0E);
    public static final Color RED         = new Color(0xD1, 0x3B, 0x3B);
    public static final Color SIDEBAR_SELECTED = new Color(0x24, 0x40, 0x66);

    public static final Font FONT_TITLE   = new Font("SansSerif", Font.BOLD, 19);
    public static final Font FONT_SUBTLE  = new Font("SansSerif", Font.PLAIN, 12);
    public static final Font FONT_SECTION = new Font("SansSerif", Font.BOLD, 15);
    public static final Font FONT_BODY    = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_SMALL   = new Font("SansSerif", Font.PLAIN, 11);
    public static final Font FONT_STAT    = new Font("SansSerif", Font.BOLD, 26);
    public static final Font FONT_MONO    = new Font(Font.MONOSPACED, Font.PLAIN, 12);
    public static final Font FONT_NAV_GROUP = new Font("SansSerif", Font.BOLD, 11);
    public static final Font FONT_NAV_ITEM  = new Font("SansSerif", Font.PLAIN, 13);
    public static final Font FONT_BUTTON  = new Font("SansSerif", Font.BOLD, 13);

    // ---- layout helpers ----

    public static JPanel page(String title, String subtitle) {
        JPanel root = new JPanel(new BorderLayout(0, 14));
        root.setBackground(CONTENT_BG);
        root.setBorder(new EmptyBorder(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        head.setOpaque(false);
        JLabel t = new JLabel(title);
        t.setFont(FONT_SECTION);
        t.setForeground(TEXT_DARK);
        t.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(t);
        if (subtitle != null) {
            JLabel s = new JLabel(subtitle);
            s.setFont(FONT_SUBTLE);
            s.setForeground(TEXT_MUTED);
            s.setAlignmentX(Component.LEFT_ALIGNMENT);
            s.setBorder(new EmptyBorder(3, 0, 0, 0));
            head.add(s);
        }
        root.add(head, BorderLayout.NORTH);
        return root;
    }

    /** A simple flat card: white background, thin border, padding. No shadows, no rounding. */
    public static JPanel card() {
        JPanel p = new JPanel();
        p.setBackground(CARD_BG);
        p.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(14, 16, 14, 16)));
        return p;
    }

    public static JPanel statCard(String value, String label, Color accent) {
        JPanel p = card();
        p.setLayout(new BorderLayout(0, 4));
        JLabel top = new JLabel(" ");
        top.setPreferredSize(new Dimension(10, 3));
        top.setOpaque(true);
        top.setBackground(accent);

        JLabel valueLbl = new JLabel(value);
        valueLbl.setFont(FONT_STAT);
        valueLbl.setForeground(TEXT_DARK);

        JLabel labelLbl = new JLabel(label.toUpperCase());
        labelLbl.setFont(FONT_NAV_GROUP);
        labelLbl.setForeground(TEXT_MUTED);

        JPanel textWrap = new JPanel();
        textWrap.setOpaque(false);
        textWrap.setLayout(new BoxLayout(textWrap, BoxLayout.Y_AXIS));
        valueLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        labelLbl.setAlignmentX(Component.LEFT_ALIGNMENT);
        textWrap.add(valueLbl);
        textWrap.add(Box.createVerticalStrut(2));
        textWrap.add(labelLbl);

        p.add(top, BorderLayout.NORTH);
        p.add(textWrap, BorderLayout.CENTER);
        return p;
    }

    public static JLabel sectionLabel(String text) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SECTION);
        l.setForeground(TEXT_DARK);
        return l;
    }

    public static JLabel explainerLabel(String htmlText) {
        JLabel l = new JLabel("<html><body style='width:520px'>" + htmlText + "</body></html>");
        l.setFont(FONT_SUBTLE);
        l.setForeground(TEXT_MUTED);
        return l;
    }

    public static JButton primaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(ACCENT_BLUE);
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setBorder(new EmptyBorder(9, 18, 9, 18));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setOpaque(true);
        b.setBorderPainted(false);
        return b;
    }

    public static JButton secondaryButton(String text) {
        JButton b = new JButton(text);
        b.setFont(FONT_BUTTON);
        b.setBackground(Color.WHITE);
        b.setForeground(TEXT_DARK);
        b.setFocusPainted(false);
        b.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(BORDER, 1),
                new EmptyBorder(8, 16, 8, 16)));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }

    /** A small colored pill, e.g. for urgency levels or status words. */
    public static JLabel pill(String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_SMALL);
        l.setForeground(Color.WHITE);
        l.setOpaque(true);
        l.setBackground(color);
        l.setBorder(new EmptyBorder(3, 9, 3, 9));
        l.setHorizontalAlignment(SwingConstants.CENTER);
        return l;
    }

    public static Color urgencyColor(int level) {
        if (level <= 1) return RED;
        if (level == 2) return AMBER;
        return GREEN;
    }

    public static JTable table(String[] columns, Object[][] rows) {
        DefaultTableModel model = new DefaultTableModel(rows, columns) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable t = new JTable(model);
        t.setFont(FONT_BODY);
        t.setRowHeight(26);
        t.setShowGrid(false);
        t.setIntercellSpacing(new Dimension(0, 0));
        t.setSelectionBackground(new Color(0xE9, 0xF0, 0xFD));
        t.setSelectionForeground(TEXT_DARK);
        t.setFillsViewportHeight(true);
        t.getTableHeader().setFont(FONT_NAV_GROUP);
        t.getTableHeader().setForeground(TEXT_MUTED);
        t.getTableHeader().setBackground(CONTENT_BG);
        t.getTableHeader().setBorder(BorderFactory.createMatteBorder(0, 0, 1, 0, BORDER));
        t.setBorder(null);
        return t;
    }

    public static JScrollPane scroll(Component c) {
        JScrollPane sp = new JScrollPane(c);
        sp.setBorder(BorderFactory.createLineBorder(BORDER, 1));
        sp.getVerticalScrollBar().setUnitIncrement(16);
        return sp;
    }

    public static Border pad(int t, int l, int b, int r) {
        return new EmptyBorder(t, l, b, r);
    }
}
