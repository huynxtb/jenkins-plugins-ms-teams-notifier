package io.jenkins.plugins.enums;

public enum StatusColor {
    /**
     * Green when send success.
     */
    GREEN("#28A745"),

    /**
     * Yellow when send unstable.
     */
    YELLOW("#FFEB3B"),

    /**
     * Gray when send aborted.
     */
    GRAY("#A9A9A9"),

    /**
     * Red when send fail.
     */
    RED("#DC3545");

    private String color;

    StatusColor(String _color) {
        this.color = _color;
    }

    public String getColor() {
        return this.color;
    }
}