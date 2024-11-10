package task;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Represents a subtask associated with a main task. Each subtask has a unique ID,
 * title, optional description, completion status, and a reference to its parent task.
 */
public class Subtask implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private static final AtomicInteger idGenerator = new AtomicInteger(0);

    private final int id;
    private final String title;
    private String description;
    private boolean isCompleted;
    private final Task parentTask;
    private final Date startTime;
    private Date actualEndTime;

    /**
     * Constructs a new Subtask instance with a required title, optional description,
     * and a reference to its parent task. Automatically generates a unique ID
     * and sets the start time to the current date.
     *
     * @param title       The title of the subtask, cannot be null or empty.
     * @param parentTask  The parent task associated with this subtask, cannot be null.
     * @throws IllegalArgumentException if title is empty or null, or if parentTask is null.
     */
    public Subtask(String title, Task parentTask) {
        if (title == null || title.isEmpty()) {
            throw new IllegalArgumentException("Subtask title cannot be null or empty.");
        }
        if (parentTask == null) {
            throw new IllegalArgumentException("Parent task cannot be null.");
        }
        this.id = idGenerator.incrementAndGet(); // Generates a unique ID
        this.title = title;
        this.isCompleted = false;
        this.parentTask = parentTask;
        this.startTime = new Date(); // Sets start time to current time
    }

    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Marks the subtask as completed and records the actual end time.
     */
    public void completeSubtask() {
        this.isCompleted = true;
        this.actualEndTime = new Date(); // Sets end time to current time
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

    public boolean isCompleted() {
        return isCompleted;
    }

    public Task getParentTask() {
        return parentTask;
    }

    public Date getStartTime() {
        return startTime;
    }

    public Date getActualEndTime() {
        return actualEndTime;
    }

    // Override equals, hashCode, and toString methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Subtask subtask = (Subtask) o;
        return id == subtask.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "Subtask{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", isCompleted=" + isCompleted +
                ", parentTaskId=" + parentTask.getId() +
                ", startTime=" + startTime +
                ", actualEndTime=" + actualEndTime +
                '}';
    }
}