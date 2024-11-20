package frame;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.*;
import task.Subtask;
import task.Task;
import task.TaskManager;


/**
 * Frame for creating or editing a task, allowing management of subtasks.
 * Closes the main frame when opened, then reopens and refreshes it after task operations.
 */
public class TaskEditFrame extends JFrame {

  private JTextField titleField;
  private JTextArea descriptionArea;
  private JSpinner dueDateSpinner;
  private final JFrame mainFrame;
  private final List<Subtask> subtasks;
  private JPanel subtaskPanel;

  // If task is null, the frame operates in creation mode; otherwise, in edit mode.
  private final Task task;

  /**
   * Creates a new task creat frame with parent MainFrame.
   *
   * @param mainFrame parent MainFrame.
   */
  public TaskEditFrame(JFrame mainFrame) {
    this.mainFrame = mainFrame;
    this.task = null;
    this.subtasks = new ArrayList<>();
    setTitle("New Task");

    initializeUI();
  }

  /**
   * Creates a new task edit frame with parent MainFrame.
   *
   * @param mainFrame parent MainFrame.
   */
  public TaskEditFrame(JFrame mainFrame, Task task) {
    this.mainFrame = mainFrame;
    this.task = task;
    this.subtasks = task.getSubtasks();
    setTitle("Edit Task");

    initializeUI();
  }

  private void initializeUI() {
    setSize(400, 400);
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


    // Title field
    JPanel titlePanel = new JPanel(new BorderLayout());

    titlePanel.add(new JLabel("Title:"), BorderLayout.WEST);
    titleField = new JTextField();
    titlePanel.add(titleField, BorderLayout.CENTER);

    if (task != null) {
      titleField.setText(task.getTitle());
      titleField.setEditable(false);
    }

    JPanel formPanel = new JPanel(new BorderLayout(10, 10));
    formPanel.add(titlePanel, BorderLayout.NORTH);

    // Description area
    JPanel descriptionPanel = new JPanel(new BorderLayout());
    descriptionPanel.add(new JLabel("Description:"), BorderLayout.WEST);
    descriptionArea = new JTextArea(3, 20);
    descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
    formPanel.add(descriptionPanel, BorderLayout.CENTER);
    if (task != null) {
      descriptionArea.setText(task.getDescription());
    }

    // Due date
    JPanel dueDatePanel = new JPanel(new BorderLayout());
    dueDatePanel.add(new JLabel("Due Date:"), BorderLayout.WEST);
    dueDateSpinner = new JSpinner(new SpinnerDateModel());
    JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dueDateSpinner, "yyyy-MM-dd HH:mm");
    dueDateSpinner.setEditor(dateEditor);
    dueDatePanel.add(dueDateSpinner, BorderLayout.CENTER);
    formPanel.add(dueDatePanel, BorderLayout.SOUTH);
    if (task != null) {
      dueDateSpinner.setValue(task.getExpectedEndTime());
    }

    add(formPanel, BorderLayout.NORTH);

    // Subtask area
    JPanel subtaskArea = new JPanel(new BorderLayout());
    JButton addSubtaskButton = new JButton("Add Subtask");
    addSubtaskButton.addActionListener(this::addSubtask);
    subtaskArea.add(addSubtaskButton, BorderLayout.NORTH);

    subtaskPanel = new JPanel();
    subtaskPanel.setLayout(new BoxLayout(subtaskPanel, BoxLayout.Y_AXIS));
    subtaskArea.add(new JScrollPane(subtaskPanel), BorderLayout.CENTER);

    add(subtaskArea, BorderLayout.CENTER);

    displaySubtasks();

    // Buttons for task operations
    JPanel buttonPanel = new JPanel();
    JButton actionButton = new JButton(task == null ? "Create" : "Edit");
    actionButton.addActionListener(e -> {
      if (task == null) {
        createTask();
      } else {
        editTask();
      }
    });

    JButton cancelButton = new JButton("Cancel");
    cancelButton.addActionListener(e -> returnToMainFrame());

    buttonPanel.add(actionButton);
    buttonPanel.add(cancelButton);

    add(buttonPanel, BorderLayout.SOUTH);
  }

  /**
   * Opens a dialog to create a new subtask.
   * Allows setting a title and optional description for the subtask.
   */
  private void addSubtask(ActionEvent e) {
    JTextField subtaskTitleField = new JTextField(10);
    JTextArea subtaskDescField = new JTextArea(3, 20);
    Object[] message = {
      "Subtask Title:", subtaskTitleField,
      "Subtask Description:", new JScrollPane(subtaskDescField)
    };

    int option = JOptionPane.showConfirmDialog(this, message,
            "New Subtask", JOptionPane.OK_CANCEL_OPTION);
    if (option == JOptionPane.OK_OPTION) {
      String title = subtaskTitleField.getText().trim();
      if (title.isEmpty()) {
        JOptionPane.showMessageDialog(this, "Subtask title cannot be empty.",
                "Input Error", JOptionPane.ERROR_MESSAGE);
        return;
      }
      Subtask subtask = new Subtask(title, null);
      subtask.setDescription(subtaskDescField.getText().trim());
      subtasks.add(subtask);
      displaySubtasks();
    }
  }

  private void displaySubtasks() {
    subtaskPanel.removeAll();
    for (Subtask subtask : subtasks) {
      JPanel subtaskItem = new JPanel(new BorderLayout());
      String taskInfo = String.format(
              "<html><b>%s</b><br/>Description: %s</html>",
              subtask.getTitle(),
              subtask.getDescription() != null ? subtask.getDescription() : "None"
      );
      subtaskItem.add(new JLabel(taskInfo), BorderLayout.WEST);

      JButton deleteButton = new JButton("Delete");
      deleteButton.addActionListener(e -> {
        subtasks.remove(subtask);
        displaySubtasks();
      });
      subtaskItem.add(deleteButton, BorderLayout.EAST);

      subtaskPanel.add(subtaskItem);
    }
    subtaskPanel.revalidate();
    subtaskPanel.repaint();
  }

  private void createTask() {
    String title = titleField.getText().trim();
    String description = descriptionArea.getText().trim();
    Date dueDate = (Date) dueDateSpinner.getValue();

    if (title.isEmpty()) {
      JOptionPane.showMessageDialog(this, "Title cannot be empty.",
              "Input Error", JOptionPane.ERROR_MESSAGE);
      return;
    }

    Task newTask = new Task(title, dueDate);
    newTask.setDescription(description);

    for (Subtask subtask : subtasks) {
      subtask.setParentTask(newTask);
      newTask.addSubtask(subtask);
    }

    TaskManager.getInstance().addTask(newTask);
    JOptionPane.showMessageDialog(this, "Task created successfully!",
            "Success", JOptionPane.INFORMATION_MESSAGE);

    returnToMainFrame();
  }

  private void editTask() {
    if (task == null) {
      return;
    }
    task.setDescription(descriptionArea.getText().trim());
    task.setDueDate((Date) dueDateSpinner.getValue());
    task.clearSubtasks();

    for (Subtask subtask : subtasks) {
      subtask.setParentTask(task);
      task.addSubtask(subtask);
    }

    TaskManager.getInstance().triggerAutoSave();
    JOptionPane.showMessageDialog(this, "Task edited successfully.",
            "Success", JOptionPane.INFORMATION_MESSAGE);

    returnToMainFrame();
  }

  private void returnToMainFrame() {
    mainFrame.setVisible(true);
    ((MainFrame) mainFrame).refreshTasks();
    dispose();
  }
}