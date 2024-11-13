package frame;

import task.Task;
import task.TaskManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;

/**
 * MainFrame displays and manages the task list UI. It includes sorting options
 * and allows navigation to other frames for creating and editing tasks.
 */
public class MainFrame extends JFrame {

    private JPanel taskListPanel;

    /**
     * Constructs the main frame with options to view, sort, and manage tasks.
     */
    public MainFrame() {
        initializeUI();
    }

    private void initializeUI() {
        setTitle("待办事项");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(800, 600);
        setLocationRelativeTo(null);

        // Create main panel with border
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

        // Task list panel with scroll support
        taskListPanel = new JPanel();
        taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
        taskListPanel.setBackground(Color.cyan);
        JScrollPane scrollPane = new JScrollPane(taskListPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        // Populate task list
        populateTaskList(TaskManager.getInstance().getUncompletedTasks());

        // Menu bar with sorting options and settings
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        initializeMenuBar(menuBar);
    }

    /**
     * Initialize the menu bar.
     *
     * @param menuBar the menu bar which need to be initialized
     */
    private void initializeMenuBar(JMenuBar menuBar) {
        JMenu optionsMenu = new JMenu("选项");
        menuBar.add(optionsMenu);

        JMenuItem newTaskMenuItem = new JMenuItem("新建待办事项");
        newTaskMenuItem.addActionListener(e -> openNewTaskFrame());
        optionsMenu.add(newTaskMenuItem);

        JMenuItem settingsMenuItem = new JMenuItem("设置");
        settingsMenuItem.addActionListener(e -> openSettingsFrame());
        optionsMenu.add(settingsMenuItem);

        JMenu sortMenu = new JMenu("排序方式");
        menuBar.add(sortMenu);

        JMenuItem sortByCreation = new JMenuItem("按创建时间");
        sortByCreation.addActionListener(
                e -> populateTaskList(TaskManager.getInstance().getTasksByCreationDate()));
        sortMenu.add(sortByCreation);

        JMenuItem sortByDueDate = new JMenuItem("按截止时间");
        sortByDueDate.addActionListener(
                e -> populateTaskList(TaskManager.getInstance().getTasksByDueDate()));
        sortMenu.add(sortByDueDate);

        JMenuItem sortByHeat = new JMenuItem("按任务热度");
        sortByHeat.addActionListener(
                e -> populateTaskList(TaskManager.getInstance().getTasksByHeatIndex()));
        sortMenu.add(sortByHeat);
    }

    /**
     * Populates the task list panel with task information.
     *
     * @param tasks the list of tasks to display
     */
    private void populateTaskList(List<Task> tasks) {
        taskListPanel.removeAll();
        for (Task task : tasks) {
            JPanel taskPanel = new JPanel(new BorderLayout());
            taskPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
            taskPanel.setPreferredSize(new Dimension(taskListPanel.getWidth(), 90)); // Fixed height

            // Task information
            String taskInfo = String.format(
                    "<html><b>%s</b><br/>描述: %s<br/>热度: %d<br/>截止时间: %s</html>",
                    task.getTitle(),
                    task.getDescription() != null ? task.getDescription() : "无",
                    task.getHeatIndex(),
                    task.getExpectedEndTime()
            );
            JLabel taskLabel = new JLabel(taskInfo);
            taskPanel.add(taskLabel, BorderLayout.CENTER);

            // Details button
            JButton detailsButton = new JButton("查看详情");
            detailsButton.addActionListener(e -> openTaskDetailsFrame(task));
            taskPanel.add(detailsButton, BorderLayout.EAST);

            taskPanel.setBackground(Color.WHITE);
            taskListPanel.add(taskPanel);
        }
        taskListPanel.revalidate();
        taskListPanel.repaint();
    }

    private void openSettingsFrame() {
        JOptionPane.showMessageDialog(this, "跳转至设置界面");
    }

    private void openTaskDetailsFrame(Task task) {
        JOptionPane.showMessageDialog(this, "跳转至任务详情界面: " + task.getTitle());
    }

    private void openNewTaskFrame() {
        NewTaskFrame newTaskFrame = new NewTaskFrame(this);
        newTaskFrame.setVisible(true);
        setVisible(false);
    }

    /** Refreshes the task list displayed in the task list panel. */
    public void refreshTaskList() {
        populateTaskList(TaskManager.getInstance().getUncompletedTasks());
    }
}