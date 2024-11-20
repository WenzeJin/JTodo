package task;

import java.io.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import utils.Settings;

/**
 * Manages a collection of Task objects, providing operations to add, remove, and complete tasks.
 * Includes methods for saving to and loading from a file for persistent storage.
 */
public class TaskManager {

  /**
   * Query mode options.
   */
  public enum QueryMode {
    ALL,
    COMPLETE,
    INCOMPLETE,
  }

  /**
   * Sort mode options.
   */
  public enum SortMode {
    CREATION,
    CREATION_R,
    DUE,
    DUE_R,
    HEAT,
    COMPLETE,
    COMPLETE_R,
  }

  private static TaskManager instance;

  // Executor for async autosave
  private final ExecutorService autoSaveExecutor = Executors.newSingleThreadExecutor();

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

  /**
   * Get the only instance of class TaskManager.
   * Please only use this method whenever you want to access TaskManager.
   * Do Not use variables to store the return object.
   *
   * @return the only instance.
   */
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
      autoSaveExecutor.submit(() -> {
        try {
          saveTasksToFile(Settings.getInstance().getTaskSavePath());
        } catch (IOException e) {
          System.err.println("Failed to save tasks: " + e.getMessage());
        }
      });
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
  public void setTaskCompleted(Task task, boolean completed) {
    task.setCompleted(completed);
    triggerAutoSave();
  }

  /**
   * Marks a specified task as completed.
   *
   * @param task The task to complete.
   */
  public void updateHeat(Task task) {
    task.increaseHeat();
    triggerAutoSave();
  }


  /**
   * Marks a specified subtask as completed.
   *
   * @param subtask The subtask to complete.
   */
  public void setSubtaskCompleted(Subtask subtask, boolean completed) {
    subtask.setCompleted(completed);
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
   * Retrieves a list of tasks sorted by sort mode and filtered by query mode.
   *
   * @param queryMode query mode, default ALL
   * @param sortMode sort mode, default CREATION
   *
   * @return a list of tasks sorted by creation time
   */
  public List<Task> getTasks(QueryMode queryMode, SortMode sortMode) {
    List<Task> tasks = getTasksByQueryMode(queryMode);
    if (!tasks.isEmpty()) {
      switch (sortMode) {
        case CREATION:
          tasks.sort(Comparator.comparing(Task::getStartTime));
          break;
        case CREATION_R:
          tasks.sort(Comparator.comparing(Task::getStartTime).reversed());
          break;
        case DUE:
          tasks.sort(Comparator.comparing(Task::getExpectedEndTime));
          break;
        case DUE_R:
          tasks.sort(Comparator.comparing(Task::getExpectedEndTime).reversed());
          break;
        case HEAT:
          tasks.sort(Comparator.comparing(Task::getHeatIndex).reversed());
          break;
        case COMPLETE:
          tasks = getTasksByQueryMode(QueryMode.COMPLETE);
          tasks.sort(Comparator.comparing(Task::getActualEndTime));
          break;
        case COMPLETE_R:
          tasks = getTasksByQueryMode(QueryMode.COMPLETE);
          tasks.sort(Comparator.comparing(Task::getActualEndTime).reversed());
          break;
        default:
          break;
      }
    }
    return tasks;
  }

  /**
   * Retrieves a list of tasks sorted by default sort mode and filtered by default query mode.
   *
   *
   * @return a list of tasks sorted by creation time
   */
  public List<Task> getTasks() {
    return getTasks(QueryMode.ALL, SortMode.CREATION);
  }


  /**
   * Retrieves tasks with selected query mode.
   *
   * @return a list of completed tasks
   */
  private List<Task> getTasksByQueryMode(QueryMode mode) {
    return switch (mode) {
      case ALL -> new ArrayList<>(tasks);
      case COMPLETE -> new ArrayList<>(tasks).stream()
              .filter(Task::isCompleted).collect(Collectors.toList());
      case INCOMPLETE -> new ArrayList<>(tasks).stream()
              .filter(task -> !task.isCompleted()).collect(Collectors.toList());
    };
  }

  /**
   * Saves the current list of tasks to a specified file for persistent storage.
   *
   * @param fileName The name of the file to save tasks to.
   * @throws IOException if an I/O error occurs.
   */
  public synchronized void saveTasksToFile(String fileName) throws IOException {
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