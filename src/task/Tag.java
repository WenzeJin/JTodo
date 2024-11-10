package task;

import java.io.Serializable;
import java.util.Objects;

/**
 * Represents a tag that can be associated with tasks.
 * Each tag includes a name, a color, and an optional icon.
 */
public class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String color;
    private String icon;

    /**
     * Constructs a Tag with a specified name, color, and icon.
     *
     * @param name  The name of the tag, cannot be null or empty.
     * @param color The color code of the tag, must be a valid hex color string.
     * @param icon  The icon representation of the tag, optional.
     * @throws IllegalArgumentException if name is empty or null, or if color is not in hex format.
     */
    public Tag(String name, String color, String icon) {
        setName(name);
        setColor(color);
        this.icon = icon;
    }

    /**
     * Sets the name of the tag.
     *
     * @param name The new name for the tag.
     * @throws IllegalArgumentException if name is empty or null.
     */
    public void setName(String name) {
        if (name == null || name.isEmpty()) {
            throw new IllegalArgumentException("Tag name cannot be null or empty.");
        }
        this.name = name;
    }

    /**
     * Sets the color of the tag.
     *
     * @param color The color code of the tag, must be a valid hex color string (e.g., "#FFFFFF").
     * @throws IllegalArgumentException if color is not a valid hex color code.
     */
    public void setColor(String color) {
        if (color == null || !color.matches("^#([A-Fa-f0-9]{6})$")) {
            throw new IllegalArgumentException("Color must be a valid hex color code (e.g., #FFFFFF).");
        }
        this.color = color;
    }

    /**
     * Sets the icon for the tag.
     *
     * @param icon The icon representation of the tag.
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }

    // Getters for each field

    public String getName() {
        return name;
    }

    public String getColor() {
        return color;
    }

    public String getIcon() {
        return icon;
    }

    // Override equals, hashCode, and toString methods

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Tag tag = (Tag) o;
        return Objects.equals(name, tag.name) &&
                Objects.equals(color, tag.color) &&
                Objects.equals(icon, tag.icon);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, color, icon);
    }

    @Override
    public String toString() {
        return "Tag{" +
                "name='" + name + '\'' +
                ", color='" + color + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }
}