package io.jenkins.plugins.dto;

import java.util.ArrayList;

public class ContentDto {
    private String type;
    private String context;
    private String summary;
    private String title;
    private ArrayList<SectionDto> sections;

    public ContentDto(String title, ArrayList<SectionDto> sections) {
        this.title = title;
        this.sections = sections;
        this.type = "MessageCard";
        this.context = "https://schema.org/extensions";
        this.summary = "Summary";
    }

    public String getType() {
        return this.type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getContext() {
        return this.context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getSummary() {
        return this.summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<SectionDto> getSections() {
        return this.sections;
    }

    public void setSections(ArrayList<SectionDto> sections) {
        this.sections = sections;
    }
}