package task;

import java.io.Serial;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a task with a unique ID, title, optional description and tag, and expected end time.
 * A task can have subtasks, be marked as complete, and track its activity through a heat index.
 */
public class Task implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    private final int id;
    private final String title;
    private String description; // Optional
    private boolean isCompleted;
    private final List<Subtask> subtasks;
    private Tag tag; // Optional
    private final Date startTime;
    private final Date expectedEndTime;
    private Date actualEndTime;
    private int heatIndex;

    /**
     * Constructs a new Task instance with required title and expected end time.
     * Automatically sets the start time to the current date and generates a unique ID.
     *
     * @param title           The title of the task, cannot be null or empty.
     * @param expectedEndTime The expected end time of the task, cannot be null.
     * @throws IllegalArgumentException if title is empty or null, or if expectedEndTime is null.
     */
    public Task(String title, Date expectedEndTime) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Task title cannot be null or empty.");
        }
        if (expectedEndTime == null) {
            throw new IllegalArgumentException("Expected end time cannot be null.");
        }
        this.id = idGenerator.incrementAndGet(); // Generates a unique ID
        this.title = title;
        this.isCompleted = false;
        this.subtasks = new ArrayList<>();
        this.startTime = new Date(); // Sets start time to current time
        this.expectedEndTime = expectedEndTime;
        this.heatIndex = 0;
    }

    // Optional setters for description and tag

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTag(Tag tag) {
        this.tag = tag;
    }

    /**
     * Updates the heat index based on the frequency of modifications.
     */
    public void increaseHeat() {
        // Logic to calculate and update the heat index (e.g., based on view and update frequency)
        heatIndex++;
    }

    /**
     * Adds a subtask to the current task.
     *
     * @param subtask The subtask to add.
     */
    public void addSubtask(Subtask subtask) {
        subtasks.add(subtask);
    }

    /**
     * Marks the task as completed if all subtasks are completed and sets the actual end time.
     */
    public void setCompleted(boolean completed) {
        this.isCompleted = completed;
        if(completed)
            this.actualEndTime = new Date(); // Sets actual end time to the current time
    }

    // Getters for each field

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public Tag getTag() {
        return tag;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getExpectedEndTime() {
        return expectedEndTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public List<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks);
    }

    public int getHeatIndex() {
        return heatIndex;
    }

    // Override equals, hashCode, and toString methods if necessary

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Task task = (Task) o;
        return id == task.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", startTime=" + startTime +
                ", expectedEndTime=" + expectedEndTime +
                ", actualEndTime=" + actualEndTime +
                ", heatIndex=" + heatIndex +
                '}';
    }
}