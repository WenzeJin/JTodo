package frame;

import task.Task;
import task.TaskManager;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.List;
import java.util.Date;

/**
 * MainFrame displays and manages the task list UI. It includes sorting options
 * and allows navigation to other frames for creating and editing tasks.
 */
public class MainFrame extends JFrame {

    private JPanel taskListPanel;

    //private JLabel modeLabel;

    private TaskManager.QueryMode queryMode = TaskManager.QueryMode.ALL;

    private TaskManager.SortMode sortMode = TaskManager.SortMode.CREATION;

    /**
     * Constructs the main frame with options to view, sort, and manage tasks.
     */
    public MainFrame() {
        initializeUI();
    }

    private void initializeUI() {
        // Check if system OS is macOS
        if (System.getProperty("os.name").toLowerCase().contains("mac")) {
            // Use system menu bar on macOS
            System.setProperty("apple.laf.useScreenMenuBar", "true");
        }

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

        // Refresh tasks
        refreshTasks();

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

        // Sort Menu

        JMenu sortMenu = new JMenu("排序方式");
        menuBar.add(sortMenu);

        JMenuItem sortByCreation = new JMenuItem("按创建时间(升序)");
        sortByCreation.addActionListener(
                e -> setSortMode(TaskManager.SortMode.CREATION));
        sortMenu.add(sortByCreation);

        JMenuItem sortByCreationR = new JMenuItem("按创建时间(降序)");
        sortByCreationR.addActionListener(
                e -> setSortMode(TaskManager.SortMode.CREATION_R));
        sortMenu.add(sortByCreationR);

        JMenuItem sortByDueDate = new JMenuItem("按截止时间(升序)");
        sortByDueDate.addActionListener(
                e -> setSortMode(TaskManager.SortMode.DUE));
        sortMenu.add(sortByDueDate);

        JMenuItem sortByDueDateR = new JMenuItem("按截止时间(降序)");
        sortByDueDateR.addActionListener(
                e -> setSortMode(TaskManager.SortMode.DUE_R));
        sortMenu.add(sortByDueDateR);

        JMenuItem sortByCompleteDate = new JMenuItem("按完成时间(升序,仅限已完成)");
        sortByCompleteDate.addActionListener(
                e -> setSortMode(TaskManager.SortMode.COMPLETE));
        sortMenu.add(sortByCompleteDate);

        JMenuItem sortByCompleteDateR = new JMenuItem("按完成时间(升序,仅限已完成)");
        sortByCompleteDateR.addActionListener(
                e -> setSortMode(TaskManager.SortMode.COMPLETE_R));
        sortMenu.add(sortByCompleteDate);

        JMenuItem sortByHeat = new JMenuItem("按任务热度");
        sortByHeat.addActionListener(
                e -> setSortMode(TaskManager.SortMode.HEAT));
        sortMenu.add(sortByHeat);

        // Filter Menu

        JMenu filterMenu = new JMenu("过滤方式");
        menuBar.add(filterMenu);

        JMenuItem queryAll = new JMenuItem("全部");
        queryAll.addActionListener(
                e -> setQueryMode(TaskManager.QueryMode.ALL));
        filterMenu.add(queryAll);

        JMenuItem queryComplete = new JMenuItem("已完成");
        queryComplete.addActionListener(
                e -> setQueryMode(TaskManager.QueryMode.COMPLETE));
        filterMenu.add(queryComplete);

        JMenuItem queryIncomplete = new JMenuItem("未完成");
        queryIncomplete.addActionListener(
                e -> setQueryMode(TaskManager.QueryMode.INCOMPLETE));
        filterMenu.add(queryIncomplete);

    }

    private void setSortMode(TaskManager.SortMode sortMode) {
        this.sortMode = sortMode;
        refreshTasks();
    }

    private void setQueryMode(TaskManager.QueryMode queryMode) {
        this.queryMode = queryMode;
        refreshTasks();
    }

    public void refreshTasks() {
        List<Task> tasks = TaskManager.getInstance().getTasks(queryMode, sortMode);
        populateTaskList(tasks);
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
            Date currentDate = new Date();
            if(task.isCompleted()) {
                taskLabel.setForeground(Color.GREEN);
            } else if(currentDate.after(task.getExpectedEndTime())) {
                taskLabel.setForeground(Color.RED);
            }

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
        if(!task.isCompleted())
            TaskManager.getInstance().updateHeat(task);
        TaskDetailFrame taskDetailFrame = new TaskDetailFrame(this, task);
        taskDetailFrame.setVisible(true);
    }

    private void openNewTaskFrame() {
        TaskEditFrame taskEditFrame = new TaskEditFrame(this);
        taskEditFrame.setVisible(true);
        setVisible(false);
    }
}