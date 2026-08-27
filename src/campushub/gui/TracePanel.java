package campushub.gui;

import campushub.trace.Traces;

import javax.swing.*;
import java.awt.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import static campushub.gui.UIComponents.*;

/**
 * Trace Tables page - the six required trace tables (campushub.trace.Traces),
 * generated live from the real running code, shown in a scrollable
 * monospaced panel instead of a raw console dump.
 */
public class TracePanel extends JPanel {

    private JTextArea traceArea;

    public TracePanel(AppContext ctx) {
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Trace Tables");
        JLabel sub = explainerLabel(
                "Step-by-step evidence generated directly from the real running code "
                + "(campushub.trace.Traces) - not typed up by hand, so it is guaranteed to "
                + "match what the system actually does.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel controls = card();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 8, 6));
        String[] options = {"Binary search", "Insertion sort", "Merge sort", "Dijkstra", "Kruskal", "Knapsack DP", "All six"};
        JComboBox<String> box = new JComboBox<>(options);
        box.setPreferredSize(new Dimension(180, 30));
        JButton run = primaryButton("Show Trace");
        run.addActionListener(e -> showTrace(box.getSelectedIndex()));
        controls.add(box);
        controls.add(run);

        traceArea = new JTextArea();
        traceArea.setEditable(false);
        traceArea.setFont(FONT_MONO);
        traceArea.setBackground(Color.WHITE);
        traceArea.setMargin(new Insets(12, 12, 12, 12));

        JPanel wrapper = new JPanel(new BorderLayout(0, 12));
        wrapper.setOpaque(false);
        wrapper.add(controls, BorderLayout.NORTH);
        wrapper.add(scroll(traceArea), BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);

        showTrace(0);
    }

    private void showTrace(int choice) {
        String captured = captureSystemOut(() -> {
            switch (choice) {
                case 0: Traces.binarySearchTrace(); break;
                case 1: Traces.insertionSortTrace(); break;
                case 2: Traces.mergeSortTrace(); break;
                case 3: Traces.dijkstraTrace(); break;
                case 4: Traces.kruskalTrace(); break;
                case 5: Traces.knapsackTrace(); break;
                default: Traces.runAll();
            }
        });
        traceArea.setText(captured);
        traceArea.setCaretPosition(0);
    }

    private String captureSystemOut(Runnable task) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        System.setOut(new PrintStream(buffer));
        try { task.run(); } finally { System.setOut(original); }
        return buffer.toString();
    }
}
