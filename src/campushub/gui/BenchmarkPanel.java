package campushub.gui;

import campushub.bench.BenchmarkRunner;

import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static campushub.gui.UIComponents.*;

/**
 * Benchmark page - runs the real performance suite (campushub.bench.BenchmarkRunner,
 * six experiments, three-run averages) on a background thread so the
 * window stays responsive, then reads the resulting CSV files straight
 * back from results/ into real tables. No external charting library.
 */
public class BenchmarkPanel extends JPanel {

    private final JPanel body;
    private final JLabel status;

    public BenchmarkPanel(AppContext ctx) {
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Benchmarks");
        JLabel sub = explainerLabel(
                "Six required performance experiments (search, sort, hash load factor, BST vs "
                + "balanced tree, heap dispatch, graph algorithms), each averaged over 3 runs "
                + "and written to results/*.csv - the same evidence used in the report.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel controls = new JPanel(new FlowLayout(FlowLayout.LEFT, 12, 6));
        controls.setOpaque(false);
        JButton run = primaryButton("Run Benchmarks");
        status = new JLabel("Not yet run this session.");
        status.setFont(FONT_SUBTLE);
        status.setForeground(TEXT_MUTED);
        run.addActionListener(e -> runBenchmarks(run));
        controls.add(run);
        controls.add(status);

        body = new JPanel(new BorderLayout());
        body.setOpaque(false);

        JPanel wrapper = new JPanel(new BorderLayout(0, 8));
        wrapper.setOpaque(false);
        wrapper.add(controls, BorderLayout.NORTH);
        wrapper.add(body, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        loadExistingResults();
    }

    private void runBenchmarks(JButton triggerButton) {
        triggerButton.setEnabled(false);
        status.setText("Running - this takes a little while, the window stays responsive...");
        status.setForeground(AMBER);

        new SwingWorker<Void, Void>() {
            @Override protected Void doInBackground() { BenchmarkRunner.run(); return null; }
            @Override protected void done() {
                status.setText("Complete. Results written to results/*.csv.");
                status.setForeground(GREEN);
                triggerButton.setEnabled(true);
                loadExistingResults();
            }
        }.execute();
    }

    private void loadExistingResults() {
        body.removeAll();
        String[][] files = {
                {"search_results.csv", "Search comparison"},
                {"sort_results.csv", "Sorting comparison"},
                {"hash_results.csv", "Hash table load factor"},
                {"tree_results.csv", "BST vs balanced tree"},
                {"heap_results.csv", "Heap priority dispatch"},
                {"graph_results.csv", "Graph algorithms"},
        };

        JTabbedPane tabs = new JTabbedPane();
        tabs.setFont(FONT_BODY);
        boolean anyFound = false;
        for (String[] f : files) {
            File csv = new File("results/" + f[0]);
            if (!csv.exists()) continue;
            anyFound = true;
            JTable t = readCsvAsTable(csv);
            tabs.addTab(f[1], scroll(t));
        }

        if (anyFound) {
            body.add(tabs, BorderLayout.CENTER);
        } else {
            JPanel empty = card();
            JLabel msg = new JLabel("No results yet - click \"Run Benchmarks\" to generate them.");
            msg.setFont(FONT_BODY);
            msg.setForeground(TEXT_MUTED);
            empty.add(msg);
            body.add(empty, BorderLayout.NORTH);
        }
        body.revalidate();
        body.repaint();
    }

    private JTable readCsvAsTable(File csv) {
        List<String[]> lines = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(csv))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (!line.isEmpty()) lines.add(line.split(","));
            }
        } catch (IOException e) {
            return table(new String[]{"Error"}, new Object[][]{{e.getMessage()}});
        }
        if (lines.isEmpty()) return table(new String[]{"Empty"}, new Object[0][0]);
        String[] header = lines.get(0);
        Object[][] rows = new Object[lines.size() - 1][header.length];
        for (int i = 1; i < lines.size(); i++) {
            String[] row = lines.get(i);
            for (int c = 0; c < header.length && c < row.length; c++) rows[i - 1][c] = row[c];
        }
        return table(header, rows);
    }
}
