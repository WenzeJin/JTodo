package frame;

import task.Task;
import task.TaskManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;

/**
 * Main interface for displaying and managing tasks.
 * Includes options for viewing tasks by creation date, due date, and heat index.
 */
public class MainFrame extends JFrame {

    private TaskManager taskManager;
    private JPanel taskListPanel;

    /**
     * Constructs the main frame with options to view, sort, and manage tasks.
     *
     * @param taskManager the task manager instance to manage and retrieve tasks.
     */
    public MainFrame(TaskManager taskManager) {
        this.taskManager = taskManager;
        initializeUI();
    }

    private void initializeUI() {
        setTitle("待办事项");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
        setContentPane(mainPanel);

        // Header with title and "New Task" button
        JPanel headerPanel = new JPanel(new BorderLayout());
        JLabel titleLabel = new JLabel("待办事项");
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
        headerPanel.add(titleLabel, BorderLayout.WEST);

        JButton newTaskButton = new JButton("+");
        newTaskButton.setToolTipText("新建待办事项");
        newTaskButton.addActionListener(e -> openNewTaskFrame());
        headerPanel.add(newTaskButton, BorderLayout.EAST);
        mainPanel.add(headerPanel, BorderLayout.NORTH);

        // Task list panel
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Populate task list panel
        populateTaskList(taskManager.getAllTasks());

        // Menu bar with sorting options and settings
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("选项");
        menuBar.add(menu);

        JMenuItem newTaskMenuItem = new JMenuItem("新建待办事项");
        newTaskMenuItem.addActionListener(e -> openNewTaskFrame());
        menu.add(newTaskMenuItem);

        JMenuItem settingsMenuItem = new JMenuItem("设置");
        settingsMenuItem.addActionListener(e -> openSettingsFrame());
        menu.add(settingsMenuItem);

        JMenu sortMenu = new JMenu("排序方式");
        menuBar.add(sortMenu);

        JMenuItem sortByCreation = new JMenuItem("按创建时间");
        sortByCreation.addActionListener(e -> populateTaskList(taskManager.getTasksByCreationDate()));
        sortMenu.add(sortByCreation);

        JMenuItem sortByDueDate = new JMenuItem("按截止时间");
        sortByDueDate.addActionListener(e -> populateTaskList(taskManager.getTasksByDueDate()));
        sortMenu.add(sortByDueDate);

        JMenuItem sortByHeat = new JMenuItem("按任务热度");
        sortByHeat.addActionListener(e -> populateTaskList(taskManager.getTasksByHeatIndex()));
        sortMenu.add(sortByHeat);
    }

    /**
     * Populates the task list panel with task information.
     *
     * @param tasks the list of tasks to display.
     */
    private void populateTaskList(List<Task> tasks) {
        taskListPanel.removeAll();
        for (Task task : tasks) {
            JPanel taskPanel = new JPanel(new BorderLayout());
            taskPanel.setBorder(new EmptyBorder(5, 5, 5, 5));

            // Task information
            String taskInfo = String.format("<html><b>%s</b><br/>描述: %s<br/>热度: %d<br/>截止时间: %s</html>",
                    task.getTitle(), task.getDescription() != null ? task.getDescription() : "无",
                    task.getHeatIndex(), task.getExpectedEndTime());
            JLabel taskLabel = new JLabel(taskInfo);
            taskPanel.add(taskLabel, BorderLayout.CENTER);

            // Details button
            JButton detailsButton = new JButton("查看详情");
            detailsButton.addActionListener(e -> openTaskDetailsFrame(task));
            taskPanel.add(detailsButton, BorderLayout.EAST);

            taskListPanel.add(taskPanel);
        }
        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private void openNewTaskFrame() {
        // Placeholder for opening NewTaskFrame
        JOptionPane.showMessageDialog(this, "跳转至新建任务界面");
    }

    private void openSettingsFrame() {
        // Placeholder for opening SettingsFrame
        JOptionPane.showMessageDialog(this, "跳转至设置界面");
    }

    private void openTaskDetailsFrame(Task task) {
        // Placeholder for opening TaskDetailsFrame
        JOptionPane.showMessageDialog(this, "跳转至任务详情界面: " + task.getTitle());
    }

    /**
     * Main method for testing the main frame.
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            TaskManager taskManager = new TaskManager();
            MainFrame mainFrame = new MainFrame(taskManager);
            mainFrame.setVisible(true);
        });
    }
}