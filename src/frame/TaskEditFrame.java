package frame;

import task.Subtask;
import task.Task;
import task.TaskManager;

import javax.lang.model.type.NullType;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

/**
 * Frame for creating a new task, allowing addition and management of subtasks.
 * Closes main frame upon opening, then reopens and refreshes it after task creation.
 */
public class TaskEditFrame extends JFrame {

    private JTextField titleField;
    private JTextArea descriptionArea;
    private JSpinner dueDateSpinner;
    private final JFrame mainFrame;
    private List<Subtask> subtasks;
    private JPanel subtaskPanel;

    // task == null means creation mode, else edit mode
    private final Task task;

    public TaskEditFrame(JFrame mainFrame) {
        this.mainFrame = mainFrame;
        this.task = null;
        this.subtasks = new ArrayList<>();
        setTitle("新建待办事项");

        initializeUI();
    }

    public TaskEditFrame(JFrame mainFrame, Task task) {
        this.mainFrame = mainFrame;
        this.task = task;
        this.subtasks = task.getSubtasks();
        setTitle("编辑待办事项");

        initializeUI();
    }

    private void initializeUI(){
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
        JPanel formPanel = new JPanel(new BorderLayout(10, 10));

        // Title field
        JPanel titlePanel = new JPanel(new BorderLayout());
        titlePanel.add(new JLabel("标题："), BorderLayout.WEST);
        titleField = new JTextField();
        titlePanel.add(titleField, BorderLayout.CENTER);
        formPanel.add(titlePanel, BorderLayout.NORTH);
        if (task != null) {
            titleField.setText(task.getTitle());
            titleField.setEditable(false);
        }

        // Description area
        JPanel descriptionPanel = new JPanel(new BorderLayout());
        descriptionPanel.add(new JLabel("描述："), BorderLayout.WEST);
        descriptionArea = new JTextArea(3, 20);
        descriptionPanel.add(new JScrollPane(descriptionArea), BorderLayout.CENTER);
        formPanel.add(descriptionPanel, BorderLayout.CENTER);
        if (task != null) {
            descriptionArea.setText(task.getDescription());
        }

        // Due date
        JPanel dueDatePanel = new JPanel(new BorderLayout());
        dueDatePanel.add(new JLabel("截止时间："), BorderLayout.WEST);
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
        JButton addSubtaskButton = new JButton("添加子任务");
        addSubtaskButton.addActionListener(this::addSubtask);
        subtaskArea.add(addSubtaskButton, BorderLayout.NORTH);

        subtaskPanel = new JPanel();
        subtaskPanel.setLayout(new BoxLayout(subtaskPanel, BoxLayout.Y_AXIS));
        subtaskArea.add(new JScrollPane(subtaskPanel), BorderLayout.CENTER);

        add(subtaskArea, BorderLayout.CENTER);

        // Buttons for task creation
        JPanel buttonPanel = new JPanel();
        JButton createButton = new JButton("创建");
        createButton.addActionListener(e -> createTask());

        JButton cancelButton = new JButton("取消");
        cancelButton.addActionListener(e -> returnToMainFrame());

        buttonPanel.add(createButton);
        buttonPanel.add(cancelButton);

        add(buttonPanel, BorderLayout.SOUTH);
    }

    /**
     * Opens a dialog for adding a new subtask, which includes a title and an optional description.
     * Subtasks are displayed and deletable before task confirmation.
     */
    private void addSubtask(ActionEvent e) {
        JTextField subtaskTitleField = new JTextField(10);
        JTextArea subtaskDescField = new JTextArea(3, 20);
        Object[] message = {
                "子任务标题:", subtaskTitleField,
                "子任务描述:", new JScrollPane(subtaskDescField)
        };

        int option = JOptionPane.showConfirmDialog(this, message, "新建子任务", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            String title = subtaskTitleField.getText().trim();
            if (title.isEmpty()) {
                JOptionPane.showMessageDialog(this, "子任务标题不能为空", "输入错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
            Subtask subtask = new Subtask(title, null);  // Parent task will be set later
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
                    "<html><b>%s</b><br/>描述: %s</html>",
                    subtask.getTitle(),
                    subtask.getDescription() != null ? subtask.getDescription() : "无"
            );
            subtaskItem.add(new JLabel(taskInfo), BorderLayout.WEST);

            JButton deleteButton = new JButton("删除");
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
            JOptionPane.showMessageDialog(this, "标题不能为空", "输入错误", JOptionPane.ERROR_MESSAGE);
            return;
        }

        Task newTask = new Task(title, dueDate);
        newTask.setDescription(description);

        for (Subtask subtask : subtasks) {
            subtask.setParentTask(newTask);
            newTask.addSubtask(subtask);
        }

        TaskManager.getInstance().addTask(newTask);
        JOptionPane.showMessageDialog(this, "任务创建成功！", "成功", JOptionPane.INFORMATION_MESSAGE);

        returnToMainFrame();
    }

    private void editTask() {
        if (task == null) {
            return;
        }
        task.setDescription(descriptionArea.getText().trim());
        //TODO: complete edit task
    }

    private void returnToMainFrame() {
        mainFrame.setVisible(true);
        ((MainFrame) mainFrame).refreshTasks();
        dispose();
    }
}