package frame;

import task.Subtask;
import task.Task;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.List;

public class TaskDetailFrame extends JFrame {

    private final Task task;
    private JPanel subtaskPanel;
    private final JFrame mainFrame;

    public TaskDetailFrame(JFrame mainFrame, Task task) {
        this.mainFrame = mainFrame;
        this.task = task;
        setTitle("任务详情");
        setSize(400, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new BorderLayout());

        initializeComponents();

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                returnToMainFrame();
            }
        });
    }

    private void initializeComponents() {
        JPanel infoPanel = new JPanel();
        infoPanel.setLayout(new BoxLayout(infoPanel, BoxLayout.Y_AXIS));
        infoPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Display task title
        JLabel titleLabel = new JLabel("标题: " + task.getTitle());
        titleLabel.setFont(new Font("Serif", Font.BOLD, 18));
        infoPanel.add(titleLabel);

        // Display task description
        JLabel descriptionLabel = new JLabel("<html><b>描述:</b><br>" + (task.getDescription() != null ? task.getDescription() : "无") + "</html>");
        descriptionLabel.setBorder(BorderFactory.createEmptyBorder(10, 0, 10, 0));
        infoPanel.add(descriptionLabel);

        // Display due date, start time, and actual end time
        infoPanel.add(new JLabel("开始时间: " + task.getStartTime()));
        infoPanel.add(new JLabel("截止时间: " + task.getExpectedEndTime()));
        if (task.getActualEndTime() != null) {
            infoPanel.add(new JLabel("完成时间: " + task.getActualEndTime()));
        }

        // Display task heat index
        infoPanel.add(new JLabel("热度指数: " + task.getHeatIndex()));

        // Task completion checkbox
        JCheckBox completeCheckBox = new JCheckBox("标记为已完成");
        completeCheckBox.setSelected(task.isCompleted());
        infoPanel.add(completeCheckBox);

        add(infoPanel, BorderLayout.NORTH);

        // Subtasks section
        JPanel subtaskArea = new JPanel(new BorderLayout());
        subtaskPanel = new JPanel();
        subtaskPanel.setLayout(new BoxLayout(subtaskPanel, BoxLayout.Y_AXIS));
        subtaskArea.setBorder(BorderFactory.createTitledBorder("子任务"));

        displaySubtasks();

        subtaskArea.add(new JScrollPane(subtaskPanel), BorderLayout.CENTER);
        add(subtaskArea, BorderLayout.CENTER);

        // Close button
        JButton closeButton = new JButton("关闭");
        closeButton.addActionListener(e -> dispose());
        JPanel buttonPanel = new JPanel();
        buttonPanel.add(closeButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Display each subtask with a checkbox showing completion status, non-editable.
     */
    private void displaySubtasks() {
        subtaskPanel.removeAll();

        List<Subtask> subtasks = task.getSubtasks();
        for (Subtask subtask : subtasks) {
            JPanel subtaskItem = new JPanel(new BorderLayout());

            JCheckBox subtaskCheckBox = new JCheckBox(subtask.getTitle());
            subtaskCheckBox.setSelected(subtask.isCompleted());

            JLabel subtaskDescLabel = new JLabel("<html><i>" + (subtask.getDescription() != null ? subtask.getDescription() : "无描述") + "</i></html>");
            subtaskDescLabel.setBorder(BorderFactory.createEmptyBorder(0, 10, 0, 0));

            subtaskItem.add(subtaskCheckBox, BorderLayout.WEST);
            subtaskItem.add(subtaskDescLabel, BorderLayout.CENTER);
            subtaskPanel.add(subtaskItem);
        }

        subtaskPanel.revalidate();
        subtaskPanel.repaint();
    }

    private void returnToMainFrame() {
        mainFrame.setVisible(true);
        ((MainFrame) mainFrame).refreshTasks();
        dispose();
    }
}