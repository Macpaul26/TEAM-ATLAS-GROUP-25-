package campushub.gui;

import campushub.algo.Sorting;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static campushub.gui.UIComponents.*;

/**
 * Sort Lab - selection, insertion, merge, and quicksort
 * (campushub.algo.Sorting), all from scratch, run on a user-supplied
 * array so the same result can be reproduced live in a defense.
 */
public class SortPanel extends JPanel {

    private JTextField arrayField;
    private JComboBox<String> algoBox;
    private JPanel resultHolder;

    public SortPanel(AppContext ctx) {
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Sort Lab");
        JLabel sub = explainerLabel(
                "Four sorting strategies, all implemented from scratch: selection, insertion, "
                + "merge, and quicksort. Same input, same correct result, very different paths "
                + "to get there.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel controls = card();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));
        arrayField = new JTextField("29,10,14,37,13,1,45,22", 22);
        arrayField.setPreferredSize(new Dimension(240, 30));
        algoBox = new JComboBox<>(new String[]{"Selection", "Insertion", "Merge", "Quick", "All four"});

        controls.add(labeled("Array (comma-separated)", arrayField));
        controls.add(labeled("Algorithm", algoBox));

        JButton run = primaryButton("Run Sort");
        run.addActionListener(e -> runSort());
        controls.add(run);

        resultHolder = new JPanel();
        resultHolder.setOpaque(false);
        resultHolder.setLayout(new BoxLayout(resultHolder, BoxLayout.Y_AXIS));

        JPanel wrapper = new JPanel(new BorderLayout());
        wrapper.setOpaque(false);
        wrapper.add(controls, BorderLayout.NORTH);
        wrapper.add(resultHolder, BorderLayout.CENTER);
        add(wrapper, BorderLayout.CENTER);
    }

    private JPanel labeled(String label, JComponent field) {
        JPanel p = new JPanel();
        p.setOpaque(false);
        p.setLayout(new BoxLayout(p, BoxLayout.Y_AXIS));
        JLabel l = new JLabel(label);
        l.setFont(FONT_SMALL);
        l.setForeground(TEXT_MUTED);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        field.setAlignmentX(Component.LEFT_ALIGNMENT);
        p.add(l);
        p.add(field);
        return p;
    }

    private void runSort() {
        resultHolder.removeAll();
        Integer[] base;
        try {
            String[] parts = arrayField.getText().split(",");
            base = new Integer[parts.length];
            for (int i = 0; i < parts.length; i++) base[i] = Integer.parseInt(parts[i].trim());
        } catch (NumberFormatException e) {
            JPanel p = card();
            p.setAlignmentX(Component.LEFT_ALIGNMENT);
            JLabel err = new JLabel("Please enter whole numbers separated by commas, e.g. 29,10,14,37");
            err.setForeground(RED);
            err.setFont(FONT_BODY);
            p.add(err);
            resultHolder.add(p);
            resultHolder.revalidate();
            resultHolder.repaint();
            return;
        }

        JPanel resultCard = card();
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);
        addLine(resultCard, "Original: " + Arrays.toString(base), TEXT_DARK);

        int choice = algoBox.getSelectedIndex();
        Integer[] a;
        if (choice == 0 || choice == 4) { a = base.clone(); Sorting.selectionSort(a); addLine(resultCard, "Selection: " + Arrays.toString(a), GREEN); }
        if (choice == 1 || choice == 4) { a = base.clone(); Sorting.insertionSort(a); addLine(resultCard, "Insertion: " + Arrays.toString(a), GREEN); }
        if (choice == 2 || choice == 4) { a = base.clone(); Sorting.mergeSort(a);     addLine(resultCard, "Merge:     " + Arrays.toString(a), GREEN); }
        if (choice == 3 || choice == 4) { a = base.clone(); Sorting.quickSort(a);     addLine(resultCard, "Quick:     " + Arrays.toString(a), GREEN); }

        resultHolder.add(Box.createVerticalStrut(14));
        resultHolder.add(resultCard);
        resultHolder.revalidate();
        resultHolder.repaint();
    }

    private void addLine(JPanel target, String text, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(FONT_MONO);
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(pad(3, 0, 3, 0));
        target.add(l);
    }
}
