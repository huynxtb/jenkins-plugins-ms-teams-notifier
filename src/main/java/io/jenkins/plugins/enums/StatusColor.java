package io.jenkins.plugins.enums;

public enum StatusColor {
    /**
     * Green when send success.
     */
    GREEN("Good"),

    /**
     * Yellow when send unstable.
     */
    YELLOW("Warning"),

    /**
     * Gray when send aborted.
     */
    GRAY("Light"),

    /**
     * Red when send fail.
     */
    RED("Attention");

    private String color;

    StatusColor(String _color) {
        this.color = _color;
    }

    public String getColor() {
        return this.color;
    }
}