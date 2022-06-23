package io.jenkins.plugins.dto;

public class AttachmentDto {
    private String contentType;
    private ContentDto content;

    public AttachmentDto(ContentDto content) {
        this.content = content;
        this.contentType = "application/vnd.microsoft.teams.card.o365connector";
    }

    public String getContentType() {return this.contentType;}

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public ContentDto getContent() {
        return this.content;
    }

    public void setContent(ContentDto content) {
        this.content = content;
    }
}