package campushub.gui;

import campushub.config.IndexParameters;
import campushub.db.CsvLoader;
import campushub.db.Database;

import javax.swing.*;
import java.awt.*;
import java.util.LinkedHashMap;
import java.util.Map;

import static campushub.gui.UIComponents.*;

/**
 * TEAM ATLAS - GROUP 25 — Ghana Campus Service Hub.
 *
 * Modern dashboard-style Swing front end. This is a presentation-layer
 * redesign only: every page calls the exact same backend classes as the
 * console menu (campushub.Main) - the same Database, the same
 * campushub.ds.* structures, the same campushub.algo.* algorithms.
 * Nothing about the project's actual logic changes here.
 *
 * Run:  java -cp bin:lib/sqlite-jdbc.jar campushub.RunGui
 */
public class CampusHubGui {

    private static final String DB_PATH = "data/campushub.db";
    private static final String DATA_DIR = "data";

    private JFrame frame;
    private CardLayout cardLayout;
    private JPanel contentPanel;
    private JPanel sidebar;
    private JLabel statusPill;
    private JLabel dbStatusLabel;
    private final Map<String, JPanel> navItems = new LinkedHashMap<>();
    private String selectedKey = "dashboard";

    public static void launch() {
        SwingUtilities.invokeLater(() -> new CampusHubGui().start());
    }

    private void start() {
        buildShell();
        frame.setVisible(true);
        loadBackendInBackground();
    }

    // ---------------------------------------------------------- shell
    private void buildShell() {
        frame = new JFrame("Ghana Campus Service Hub \u2014 TEAM ATLAS - GROUP 25");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1180, 740);
        frame.setMinimumSize(new Dimension(980, 600));
        frame.setLocationRelativeTo(null);
        frame.setLayout(new BorderLayout());

        frame.add(buildHeader(), BorderLayout.NORTH);

        sidebar = buildSidebarShell();
        JScrollPane sidebarScroll = new JScrollPane(sidebar,
                JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        sidebarScroll.setBorder(BorderFactory.createEmptyBorder());
        sidebarScroll.getViewport().setBackground(NAVY);
        sidebarScroll.getVerticalScrollBar().setUnitIncrement(16);
        frame.add(sidebarScroll, BorderLayout.WEST);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        contentPanel.setBackground(CONTENT_BG);
        contentPanel.add(loadingPanel(), "loading");
        frame.add(contentPanel, BorderLayout.CENTER);
        cardLayout.show(contentPanel, "loading");
    }

    private JPanel loadingPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        p.setBackground(CONTENT_BG);
        JLabel l = new JLabel("Loading database and campus network...");
        l.setFont(FONT_BODY);
        l.setForeground(TEXT_MUTED);
        p.add(l);
        return p;
    }

    private JPanel buildHeader() {
        JPanel header = new JPanel(new BorderLayout());
        header.setBackground(NAVY);
        header.setBorder(pad(14, 22, 14, 22));

        JPanel titleBlock = new JPanel();
        titleBlock.setOpaque(false);
        titleBlock.setLayout(new BoxLayout(titleBlock, BoxLayout.Y_AXIS));
        JLabel appName = new JLabel("Ghana Campus Service Hub");
        appName.setFont(FONT_TITLE);
        appName.setForeground(Color.WHITE);
        JLabel appSub = new JLabel("Smart Service Operations Optimizer");
        appSub.setFont(FONT_SUBTLE);
        appSub.setForeground(TEXT_ON_NAVY_MUTED);
        JLabel projectMeta = new JLabel("University of Ghana, Legon  \u00b7  DCIT 204/308 \u2014 TEAM ATLAS - GROUP 25");
        projectMeta.setFont(FONT_SMALL);
        projectMeta.setForeground(TEXT_ON_NAVY_MUTED);
        titleBlock.add(appName);
        titleBlock.add(appSub);
        titleBlock.add(Box.createVerticalStrut(4));
        titleBlock.add(projectMeta);
        header.add(titleBlock, BorderLayout.WEST);

        JPanel statusBlock = new JPanel();
        statusBlock.setOpaque(false);
        statusBlock.setLayout(new BoxLayout(statusBlock, BoxLayout.Y_AXIS));
        statusPill = new JLabel("\u25CF STARTING UP");
        statusPill.setFont(FONT_NAV_GROUP);
        statusPill.setForeground(AMBER);
        statusPill.setAlignmentX(Component.RIGHT_ALIGNMENT);
        dbStatusLabel = new JLabel("Database Connecting...");
        dbStatusLabel.setFont(FONT_SMALL);
        dbStatusLabel.setForeground(TEXT_ON_NAVY_MUTED);
        dbStatusLabel.setAlignmentX(Component.RIGHT_ALIGNMENT);
        statusBlock.add(statusPill);
        statusBlock.add(dbStatusLabel);
        header.add(statusBlock, BorderLayout.EAST);

        return header;
    }

    private JPanel buildSidebarShell() {
        JPanel p = new JPanel() {
            @Override public Dimension getPreferredSize() {
                Dimension d = super.getPreferredSize();
                return new Dimension(230, d.height);
            }
            @Override public Dimension getMaximumSize() {
                Dimension d = getPreferredSize();
                return new Dimension(230, d.height);
            }
        };
        p.setBackground(NAVY);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        p.setBorder(pad(16, 0, 16, 0));
        return p;
    }

    // ---------------------------------------------------------- backend load
    private void loadBackendInBackground() {
        new SwingWorker<AppContext, Void>() {
            @Override
            protected AppContext doInBackground() {
                IndexParameters params = new IndexParameters();
                Database db = Database.connect(DB_PATH);
                db.applySchema(DATA_DIR + "/schema.sql");
                if (db.count("locations") == 0) {
                    CsvLoader.loadAll(db, DATA_DIR);
                }
                return new AppContext(db, db.loadGraph(params.routePenaltyFactor()), db.loadLocationNames(), params);
            }

            @Override
            protected void done() {
                try {
                    AppContext ctx = get();
                    buildPagesAndNav(ctx);
                    statusPill.setText("\u25CF SYSTEM ONLINE");
                    statusPill.setForeground(new Color(0x4A, 0xDE, 0x80));
                    dbStatusLabel.setText("Database Connected");
                    cardLayout.show(contentPanel, "dashboard");
                    selectNav("dashboard");
                } catch (Exception ex) {
                    statusPill.setText("\u25CF ERROR");
                    statusPill.setForeground(RED);
                    JOptionPane.showMessageDialog(frame, "Failed to start: " + ex.getMessage(),
                            "Startup error", JOptionPane.ERROR_MESSAGE);
                }
            }
        }.execute();
    }

    // ---------------------------------------------------------- pages + nav
    private void buildPagesAndNav(AppContext ctx) {
        contentPanel.add(new DashboardPanel(ctx, this::selectAndShow), "dashboard");
        contentPanel.add(new DispatchPanel(ctx), "dispatch");
        contentPanel.add(new RoutePanel(ctx), "route");
        contentPanel.add(new ReachabilityPanel(ctx), "reach");
        contentPanel.add(new NetworkPanel(ctx), "network");
        contentPanel.add(new BudgetPanel(ctx), "budget");
        contentPanel.add(new GreedyPanel(ctx), "greedy");
        contentPanel.add(new SearchPanel(ctx), "search");
        contentPanel.add(new SortPanel(ctx), "sort");
        contentPanel.add(new TracePanel(ctx), "trace");
        contentPanel.add(new AuditPanel(ctx), "audit");
        contentPanel.add(new BenchmarkPanel(ctx), "benchmarks");
        contentPanel.add(new StatsPanel(ctx), "stats");
        contentPanel.add(new ParamsPanel(ctx), "params");

        addNavGroup("OVERVIEW");
        addNavItem("dashboard", "Dashboard");

        addNavGroup("OPERATIONS");
        addNavItem("dispatch", "Service Dispatch");
        addNavItem("route", "Fastest Route");
        addNavItem("reach", "Reachable Locations");
        addNavItem("network", "Network Optimizer");

        addNavGroup("ALGORITHM LAB");
        addNavItem("budget", "Budget Optimizer");
        addNavItem("greedy", "Greedy vs Optimal");
        addNavItem("search", "Search Lab");
        addNavItem("sort", "Sort Lab");

        addNavGroup("ANALYTICS");
        addNavItem("trace", "Trace Tables");
        addNavItem("audit", "Audit Events");
        addNavItem("benchmarks", "Benchmarks");

        addNavGroup("SYSTEM");
        addNavItem("stats", "Database Statistics");
        addNavItem("params", "Index Parameters");

        sidebar.add(Box.createVerticalGlue());
        sidebar.revalidate();
        sidebar.repaint();
    }

    private void addNavGroup(String label) {
        JLabel l = new JLabel(label);
        l.setFont(FONT_NAV_GROUP);
        l.setForeground(TEXT_ON_NAVY_MUTED);
        l.setBorder(pad(16, 20, 6, 0));
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        sidebar.add(l);
    }

    private void addNavItem(String key, String label) {
        JPanel item = new JPanel(new BorderLayout());
        item.setOpaque(true);
        item.setBackground(NAVY);
        item.setBorder(pad(9, 20, 9, 12));
        item.setAlignmentX(Component.LEFT_ALIGNMENT);
        item.setMaximumSize(new Dimension(Integer.MAX_VALUE, 38));
        item.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        JLabel l = new JLabel(label);
        l.setFont(FONT_NAV_ITEM);
        l.setForeground(TEXT_ON_NAVY);
        item.add(l, BorderLayout.WEST);

        item.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override public void mouseClicked(java.awt.event.MouseEvent e) { selectAndShow(key); }
            @Override public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!key.equals(selectedKey)) item.setBackground(NAVY_LIGHT);
            }
            @Override public void mouseExited(java.awt.event.MouseEvent e) {
                if (!key.equals(selectedKey)) item.setBackground(NAVY);
            }
        });

        navItems.put(key, item);
        sidebar.add(item);
    }

    private void selectAndShow(String key) {
        cardLayout.show(contentPanel, key);
        selectNav(key);
    }

    private void selectNav(String key) {
        selectedKey = key;
        for (Map.Entry<String, JPanel> e : navItems.entrySet()) {
            e.getValue().setBackground(e.getKey().equals(key) ? SIDEBAR_SELECTED : NAVY);
        }
    }
}
