package campushub.gui;

import campushub.algo.Searching;
import campushub.algo.Sorting;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

import static campushub.gui.UIComponents.*;

/**
 * Search Lab - linear vs binary search (campushub.algo.Searching) on a
 * user-supplied array, including a live demonstration of binary search's
 * precondition failing on unsorted input.
 */
public class SearchPanel extends JPanel {

    private JTextField arrayField;
    private JTextField targetField;
    private JComboBox<String> typeBox;
    private JPanel resultHolder;

    public SearchPanel(AppContext ctx) {
        setLayout(new BorderLayout(0, 14));
        setBackground(CONTENT_BG);
        setBorder(pad(20, 24, 20, 24));

        JPanel head = new JPanel();
        head.setOpaque(false);
        head.setLayout(new BoxLayout(head, BoxLayout.Y_AXIS));
        JLabel title = sectionLabel("Search Lab");
        JLabel sub = explainerLabel(
                "Linear search checks every item; binary search cuts the remaining candidates "
                + "in half each step but <b>requires sorted input</b>. Try binary search on an "
                + "unsorted array to see its precondition fail live.");
        title.setAlignmentX(Component.LEFT_ALIGNMENT);
        sub.setAlignmentX(Component.LEFT_ALIGNMENT);
        head.add(title);
        head.add(sub);
        add(head, BorderLayout.NORTH);

        JPanel controls = card();
        controls.setLayout(new FlowLayout(FlowLayout.LEFT, 12, 6));

        arrayField = new JTextField("5,3,8,1,9,2", 18);
        targetField = new JTextField("8", 6);
        typeBox = new JComboBox<>(new String[]{"Linear", "Binary"});
        arrayField.setPreferredSize(new Dimension(200, 30));
        targetField.setPreferredSize(new Dimension(70, 30));

        controls.add(labeled("Array (comma-separated)", arrayField));
        controls.add(labeled("Target value", targetField));
        controls.add(labeled("Search type", typeBox));

        JButton run = primaryButton("Run Search");
        run.addActionListener(e -> runSearch());
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

    private void runSearch() {
        resultHolder.removeAll();
        Integer[] arr;
        try {
            String[] parts = arrayField.getText().split(",");
            arr = new Integer[parts.length];
            for (int i = 0; i < parts.length; i++) arr[i] = Integer.parseInt(parts[i].trim());
        } catch (NumberFormatException e) {
            showMessage("Please enter whole numbers separated by commas, e.g. 5,3,8,1,9", RED);
            return;
        }
        int target;
        try { target = Integer.parseInt(targetField.getText().trim()); }
        catch (NumberFormatException e) { showMessage("Target must be a whole number.", RED); return; }

        boolean binary = typeBox.getSelectedIndex() == 1;
        JPanel resultCard = card();
        resultCard.setLayout(new BoxLayout(resultCard, BoxLayout.Y_AXIS));
        resultCard.setAlignmentX(Component.LEFT_ALIGNMENT);

        addLine(resultCard, "Array: " + Arrays.toString(arr), FONT_MONO, TEXT_DARK);

        if (binary) {
            boolean isSorted = Sorting.isSorted(arr);
            if (!isSorted) {
                addLine(resultCard, "\u26a0 This array is NOT sorted - binary search's precondition is violated.", FONT_BODY, AMBER);
            }
            int result = Searching.binarySearch(arr, target);
            addLine(resultCard, "binarySearch(" + target + ") \u2192 index " + result, FONT_BUTTON, result >= 0 ? TEXT_DARK : RED);
            if (!isSorted) {
                int linResult = Searching.linearSearch(arr, target);
                addLine(resultCard, "(for comparison) linearSearch(" + target + ") \u2192 index " + linResult, FONT_BODY, TEXT_MUTED);
                if (result != linResult) {
                    addLine(resultCard, "\u2192 That mismatch is binary search's precondition being violated.", FONT_BODY, RED);
                }
            }
        } else {
            int result = Searching.linearSearch(arr, target);
            addLine(resultCard, "linearSearch(" + target + ") \u2192 index " + result, FONT_BUTTON, result >= 0 ? TEXT_DARK : RED);
        }

        resultHolder.add(Box.createVerticalStrut(14));
        resultHolder.add(resultCard);
        resultHolder.revalidate();
        resultHolder.repaint();
    }

    private void addLine(JPanel target, String text, Font font, Color color) {
        JLabel l = new JLabel(text);
        l.setFont(font);
        l.setForeground(color);
        l.setAlignmentX(Component.LEFT_ALIGNMENT);
        l.setBorder(pad(3, 0, 3, 0));
        target.add(l);
    }

    private void showMessage(String text, Color color) {
        JPanel p = card();
        p.setAlignmentX(Component.LEFT_ALIGNMENT);
        addLine(p, text, FONT_BODY, color);
        resultHolder.add(Box.createVerticalStrut(14));
        resultHolder.add(p);
        resultHolder.revalidate();
        resultHolder.repaint();
    }
}
