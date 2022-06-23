package io.jenkins.plugins.dto;

public class SectionDto {
    private String text;

    public SectionDto(String text) {
        this.text = text;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }
}