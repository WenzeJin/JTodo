package frame;

import java.awt.*;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import task.Task;
import task.TaskManager;

/**
 * The MainFrame class displays and manages the task list user interface.
 * It includes sorting options and allows navigation to other frames
 * for creating and editing tasks.
 */
public class MainFrame extends JFrame {

  private JPanel taskListPanel;
  private TaskManager.QueryMode queryMode = TaskManager.QueryMode.ALL;
  private TaskManager.SortMode sortMode = TaskManager.SortMode.CREATION;

  /**
   * Constructs the main frame with options to view, sort, and manage tasks.
   */
  public MainFrame() {
    initializeUI();
  }

  /**
   * Initializes the user interface components for the main frame.
   */
  private void initializeUI() {
    // Enable macOS-specific settings if applicable.
    if (System.getProperty("os.name").toLowerCase().contains("mac")) {
      System.setProperty("apple.laf.useScreenMenuBar", "true");
    }

    setTitle("待办事项");
    setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    setSize(800, 600);
    setLocationRelativeTo(null);

    // Create the main panel with a border layout and padding.
    JPanel mainPanel = new JPanel(new BorderLayout());
    mainPanel.setBorder(new EmptyBorder(10, 10, 10, 10));
    setContentPane(mainPanel);

    // Add header panel with title and "New Task" button.
    JPanel headerPanel = new JPanel(new BorderLayout());
    JLabel titleLabel = new JLabel("待办事项");
    titleLabel.setFont(new Font("SansSerif", Font.BOLD, 24));
    headerPanel.add(titleLabel, BorderLayout.WEST);

    JButton newTaskButton = new JButton("+");
    newTaskButton.setToolTipText("新建待办事项");
    newTaskButton.addActionListener(e -> openNewTaskFrame());
    headerPanel.add(newTaskButton, BorderLayout.EAST);
    mainPanel.add(headerPanel, BorderLayout.NORTH);

    // Create the task list panel with scroll support.
    taskListPanel = new JPanel();
    taskListPanel.setLayout(new BoxLayout(taskListPanel, BoxLayout.Y_AXIS));
    taskListPanel.setBackground(Color.CYAN);

    JScrollPane scrollPane = new JScrollPane(taskListPanel);
    scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
    mainPanel.add(scrollPane, BorderLayout.CENTER);

    refreshTasks();

    // Set up the menu bar with sorting and filtering options.
    JMenuBar menuBar = new JMenuBar();
    setJMenuBar(menuBar);
    initializeMenuBar(menuBar);
  }

  /**
   * Initializes the menu bar with options for task management.
   *
   * @param menuBar the menu bar to initialize
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
    addSortMenuItems(sortMenu);

    JMenu filterMenu = new JMenu("过滤方式");
    menuBar.add(filterMenu);
    addFilterMenuItems(filterMenu);
  }

  /**
   * Adds sorting options to the provided menu.
   *
   * @param sortMenu the menu to which sorting options are added
   */
  private void addSortMenuItems(JMenu sortMenu) {
    sortMenu.add(createMenuItem("按创建时间(升序)",
            () -> setSortMode(TaskManager.SortMode.CREATION)));
    sortMenu.add(createMenuItem("按创建时间(降序)",
            () -> setSortMode(TaskManager.SortMode.CREATION_R)));
    sortMenu.add(createMenuItem("按截止时间(升序)",
            () -> setSortMode(TaskManager.SortMode.DUE)));
    sortMenu.add(createMenuItem("按截止时间(降序)",
            () -> setSortMode(TaskManager.SortMode.DUE_R)));
    sortMenu.add(createMenuItem("按完成时间(升序,仅限已完成)",
            () -> setSortMode(TaskManager.SortMode.COMPLETE)));
    sortMenu.add(createMenuItem("按完成时间(降序,仅限已完成)",
            () -> setSortMode(TaskManager.SortMode.COMPLETE_R)));
    sortMenu.add(createMenuItem("按任务热度",
            () -> setSortMode(TaskManager.SortMode.HEAT)));
  }

  /**
   * Adds filtering options to the provided menu.
   *
   * @param filterMenu the menu to which filtering options are added
   */
  private void addFilterMenuItems(JMenu filterMenu) {
    filterMenu.add(createMenuItem("全部", () -> setQueryMode(TaskManager.QueryMode.ALL)));
    filterMenu.add(createMenuItem("已完成", () -> setQueryMode(TaskManager.QueryMode.COMPLETE)));
    filterMenu.add(createMenuItem("未完成", () -> setQueryMode(TaskManager.QueryMode.INCOMPLETE)));
  }

  /**
   * Creates a menu item with the specified label and action.
   *
   * @param label the label for the menu item
   * @param action the action to perform when the menu item is selected
   * @return the created menu item
   */
  private JMenuItem createMenuItem(String label, Runnable action) {
    JMenuItem menuItem = new JMenuItem(label);
    menuItem.addActionListener(e -> action.run());
    return menuItem;
  }

  private void setSortMode(TaskManager.SortMode sortMode) {
    this.sortMode = sortMode;
    refreshTasks();
  }

  private void setQueryMode(TaskManager.QueryMode queryMode) {
    this.queryMode = queryMode;
    refreshTasks();
  }

  /**
   * Refreshes the task list by querying tasks from the TaskManager.
   */
  public void refreshTasks() {
    List<Task> tasks = TaskManager.getInstance().getTasks(queryMode, sortMode);
    populateTaskList(tasks);
  }

  /**
   * Populates the task list panel with the provided tasks.
   *
   * @param tasks the list of tasks to display
   */
  private void populateTaskList(List<Task> tasks) {
    taskListPanel.removeAll();
    for (Task task : tasks) {
      JPanel taskPanel = createTaskPanel(task);
      taskListPanel.add(taskPanel);
    }
    taskListPanel.revalidate();
    taskListPanel.repaint();
  }

  /**
   * Creates a panel for displaying a single task.
   *
   * @param task the task to display
   * @return the panel for the task
   */
  private JPanel createTaskPanel(Task task) {
    JPanel taskPanel = new JPanel(new BorderLayout());
    taskPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
    taskPanel.setPreferredSize(new Dimension(taskListPanel.getWidth(), 90));

    String taskInfo = String.format(
            "<html><b>%s</b><br/>描述: %s<br/>热度: %d<br/>截止时间: %s</html>",
            task.getTitle(),
            task.getDescription() != null ? task.getDescription() : "无",
            task.getHeatIndex(),
            task.getExpectedEndTime()
    );

    JLabel taskLabel = new JLabel(taskInfo);
    setTaskLabelColor(task, taskLabel);

    JButton detailsButton = new JButton("查看详情");
    detailsButton.addActionListener(e -> openTaskDetailsFrame(task));

    taskPanel.add(taskLabel, BorderLayout.CENTER);
    taskPanel.add(detailsButton, BorderLayout.EAST);
    taskPanel.setBackground(Color.WHITE);

    return taskPanel;
  }

  private void setTaskLabelColor(Task task, JLabel taskLabel) {
    if (task.isCompleted()) {
      taskLabel.setForeground(Color.GREEN);
    } else if (new Date().after(task.getExpectedEndTime())) {
      taskLabel.setForeground(Color.RED);
    }
  }

  private void openSettingsFrame() {
    JOptionPane.showMessageDialog(this, "跳转至设置界面");
  }

  private void openTaskDetailsFrame(Task task) {
    if (!task.isCompleted()) {
      TaskManager.getInstance().updateHeat(task);
    }
    TaskDetailFrame taskDetailFrame = new TaskDetailFrame(this, task);
    taskDetailFrame.setVisible(true);
  }

  private void openNewTaskFrame() {
    TaskEditFrame taskEditFrame = new TaskEditFrame(this);
    taskEditFrame.setVisible(true);
    setVisible(false);
  }
}