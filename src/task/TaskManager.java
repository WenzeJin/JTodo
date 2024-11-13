package task;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import utils.Settings;

/**
 * Manages a collection of Task objects, providing operations to add, remove, and complete tasks.
 * Includes methods for saving to and loading from a file for persistent storage.
 */
public class TaskManager implements Serializable {

    enum QueryMode {
        ALL,
        COMPLETE,
        INCOMPLETE
    }

    @Serial
    private static final long serialVersionUID = 1L;

    private static TaskManager instance;

    private List<Task> tasks;

    /**
     * Constructs a new TaskManager with an empty list of tasks.
     */
    private TaskManager() {
        this.tasks = new ArrayList<>();
        try {
            loadTasksFromFile(Settings.getInstance().getTaskSavePath());
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    public static TaskManager getInstance() {
        if (instance == null) {
            instance = new TaskManager();
        }
        return instance;
    }

    /**
     * If auto-saving is enabled by settings, save all tasks.
     *
     */
    public void triggerAutoSave() {
        if (Settings.getInstance().getAutoSaveSetting()) {
            try {
                saveTasksToFile(Settings.getInstance().getTaskSavePath());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    /**
     * Adds a new task to the task manager.
     *
     * @param task The task to add, cannot be null.
     * @throws IllegalArgumentException if task is null.
     */
    public void addTask(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task cannot be null.");
        }
        tasks.add(task);
        triggerAutoSave();
    }

    /**
     * Removes a task from the task manager.
     *
     * @param task The task to remove.
     */
    public void removeTask(Task task) {
        tasks.remove(task);
        triggerAutoSave();
    }

    /**
     * Marks a specified task as completed.
     *
     * @param task The task to complete.
     */
    public void completeTask(Task task) {
        task.completeTask();
        triggerAutoSave();
    }

    /**
     * Retrieves a task by its ID.
     *
     * @param id The unique ID of the task.
     * @return An Optional containing the task if found, or an empty Optional if not found.
     */
    public Optional<Task> getTaskById(int id) {
        return tasks.stream()
                .filter(task -> task.getId() == id)
                .findFirst();
    }

    /**
     * Retrieves a list of tasks sorted by their creation time in ascending order.
     *
     * @return a list of tasks sorted by creation time
     */
    public List<Task> getTasksByCreationDate() {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparing(Task::getStartTime));
        return sortedTasks;
    }

    /**
     * Retrieves a list of tasks sorted by their expected end time in ascending order.
     *
     * @return a list of tasks sorted by due date
     */
    public List<Task> getTasksByDueDate() {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparing(Task::getExpectedEndTime));
        return sortedTasks;
    }

    /**
     * Retrieves a list of tasks sorted by their heat index in descending order.
     *
     * @return a list of tasks sorted by heat index
     */
    public List<Task> getTasksByHeatIndex() {
        List<Task> sortedTasks = new ArrayList<>(tasks);
        sortedTasks.sort(Comparator.comparing(Task::getHeatIndex).reversed());
        return sortedTasks;
    }

    /**
     * Retrieves all uncompleted tasks.
     *
     * @return a list of uncompleted tasks
     */
    public List<Task> getUncompletedTasks() {
        return new ArrayList<>(tasks).stream()
                .filter(task -> !task.isCompleted()).toList();
    }

    /**
     * Retrieves all completed tasks.
     *
     * @return a list of completed tasks
     */
    public List<Task> getCompletedTasks() {
        return new ArrayList<>(tasks).stream()
                .filter(Task::isCompleted).toList();
    }

    /**
     * Retrieves all tasks.
     *
     * @return a list of completed tasks
     */
    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    /**
     * Saves the current list of tasks to a specified file for persistent storage.
     *
     * @param fileName The name of the file to save tasks to.
     * @throws IOException if an I/O error occurs.
     */
    public void saveTasksToFile(String fileName) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName))) {
            oos.writeObject(tasks);
        }
    }

    /**
     * Loads a list of tasks from a specified file, replacing the current task list.
     *
     * @param fileName The name of the file to load tasks from.
     * @throws IOException            if an I/O error occurs.
     * @throws ClassNotFoundException if the file content does not match expected format.
     */
    @SuppressWarnings("unchecked")
    public void loadTasksFromFile(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fileName))) {
            tasks = (List<Task>) ois.readObject();
        } catch (FileNotFoundException e) {
            System.out.println("File not found.");
        }
    }

    /**
     * Returns the list of tasks as a String for easy viewing.
     *
     * @return A String representation of all tasks.
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("Task List:\n");
        tasks.forEach(task -> sb.append(task).append("\n"));
        return sb.toString();
    }
}